package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LatestPageNoticeApiClient implements NoticeApiClient<ScrapingResultDto, DeptInfo> {

    private static final int START_PAGE_NUM = 1; // page는 인자가 1부터 시작
    private static final int ROW_NUMBERS_PER_PAGE = 20;
    private static final int ROW_NUMBERS_PER_PAGE_ALL = 100;
    private static final int LATEST_SCRAP_TIMEOUT = 2000; // 2초
    private static final int LATEST_SCRAP_ALL_TIMEOUT = 60000; // 1분

    private final NormalJsoupClient jsoupClient;

    public LatestPageNoticeApiClient(NormalJsoupClient normalJsoupClient) {
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public List<ScrapingResultDto> request(DeptInfo deptInfo) throws InternalLogicException {
        try {
            ScrapingResultDto resultDto = getScrapingResultDto(deptInfo, ROW_NUMBERS_PER_PAGE, LATEST_SCRAP_TIMEOUT);
            return List.of(resultDto);
        } catch (IOException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }
    }

    @Override
    public List<ScrapingResultDto> requestAll(DeptInfo deptInfo) throws InternalLogicException {
        try {
            // 1) 총 공지 개수 및 페이지 수 계산
            String totalUrl = buildUrlForTotalNoticeCount(deptInfo);
            int totalNoticeSize = getTotalNoticeSize(totalUrl);
            int totalPageSize = (int) Math.ceil((double) totalNoticeSize / ROW_NUMBERS_PER_PAGE_ALL);

            // 2) 첫 페이지를 base로 가져오기(1 page)
            Document baseDoc = fetchPageDoc(deptInfo, START_PAGE_NUM, ROW_NUMBERS_PER_PAGE_ALL);
            Element baseTbody = extractTbodyFromDocument(baseDoc);
            if (baseTbody == null) {
                log.warn("[SCRAP] no tbody : dept={}, title={}, url={}", deptInfo.getDeptName(), baseDoc.title(), totalUrl);
                return Collections.emptyList();
            }

            // 3) 반복문을 돌며 baseTbody에 뒷 페이지의 tr들 합치기(2 page~)
            for(int page = START_PAGE_NUM + 1; page <= totalPageSize; page++){
                Document doc = fetchPageDoc(deptInfo, page, ROW_NUMBERS_PER_PAGE_ALL);
                Elements trs = extractTrsFromDocument(doc);

                if (trs.isEmpty()) break;
                for (Element tr : trs) {
                    baseTbody.appendChild(tr.clone());
                }
            }

            // 4) 합쳐진 Document을 ScrapingResultDto로 가공하여 반환
            return List.of(new ScrapingResultDto(baseDoc, deptInfo.createUndergraduateViewUrl()));

        } catch (IOException e) {
            log.warn("Department Scrap all IOException: {}", e.getMessage());
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }

        return Collections.emptyList();
    }

    @Override
    public ScrapingResultDto requestSinglePageWithUrl(DeptInfo noticeInfo, String url) {
        throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE);
    }

    public int getTotalNoticeSize(String url) throws IOException, IndexOutOfBoundsException, NullPointerException {
        Document document = jsoupClient.get(url, LATEST_SCRAP_TIMEOUT);

        Element totalNoticeSizeElement = document.selectFirst(".util-search strong");
        if (totalNoticeSizeElement == null) {
            totalNoticeSizeElement = document.selectFirst(".total_count");
        }

        assert totalNoticeSizeElement != null;
        return Integer.parseInt(totalNoticeSizeElement.ownText());
    }

    private Element extractTbodyFromDocument(Document doc){
        return doc.selectFirst(".board-table > tbody");
    }

    private Elements extractTrsFromDocument(Document doc){
        Element tbody = extractTbodyFromDocument(doc);
        if(tbody == null){
            return new Elements();
        }
        return tbody.select("tr");
    }

    private Document getDocumentPerPage(String url, int timeout) throws IOException{
        return jsoupClient.get(url, timeout);
    }

    private Document fetchPageDoc(DeptInfo deptInfo, int page, int row) throws IOException {
        String url = deptInfo.createUndergraduateRequestUrl(page, row);
        return getDocumentPerPage(url, LATEST_SCRAP_ALL_TIMEOUT);
    }

    private String buildUrlForTotalNoticeCount(DeptInfo deptInfo) {
        return deptInfo.createUndergraduateRequestUrl(1, 1);
    }

    private ScrapingResultDto getScrapingResultDto(DeptInfo deptInfo, int rowSize, int timeout) throws IOException {
        String requestUrl = deptInfo.createUndergraduateRequestUrl(START_PAGE_NUM, rowSize);
        String viewUrl = deptInfo.createUndergraduateViewUrl();

        StopWatch stopWatch = new StopWatch(deptInfo.getDeptName() + "Request");
        stopWatch.start();

        Document document = jsoupClient.get(requestUrl, timeout);

        stopWatch.stop();
        log.debug("[{}] takes {}millis to respond", deptInfo.getDeptName(), stopWatch.getTotalTimeMillis());
        return new ScrapingResultDto(document, viewUrl);
    }
}

package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.client.JsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class LatestPageNoticeApiClient implements NoticeApiClient<ScrapingResultDto, DeptInfo> {

    private static final int PAGE_NUM = 1;    // recentPage는 pageNum 인자가 1부터 시작
    private static final int ARTICLE_NUMBERS_PER_PAGE = 12;
    private static final int LATEST_SCRAP_TIMEOUT = 60000; // 1분
    private static final int LATEST_SCRAP_ALL_TIMEOUT = 120000; // 2분

    private final JsoupClient jsoupClient;

    public LatestPageNoticeApiClient(JsoupClient normalJsoupClient) {
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public List<ScrapingResultDto> request(DeptInfo deptInfo) throws InternalLogicException {
        int size = getDeptInfoSize(deptInfo);

        List<ScrapingResultDto> reqResults = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            try {
                ScrapingResultDto resultDto = getScrapingResultDto(i, deptInfo, ARTICLE_NUMBERS_PER_PAGE, LATEST_SCRAP_TIMEOUT);
                reqResults.add(resultDto);
            } catch (IOException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
            }
        }

        return reqResults;
    }

    @Override
    public List<ScrapingResultDto> requestAll(DeptInfo deptInfo) throws InternalLogicException {
        int size = getDeptInfoSize(deptInfo);

        List<ScrapingResultDto> reqResults = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            try {
                String url = deptInfo.createRequestUrl(i, 1, 1);
                int totalNoticeSize = getTotalNoticeSize(url);

                ScrapingResultDto resultDto = getScrapingResultDto(i, deptInfo, totalNoticeSize, LATEST_SCRAP_ALL_TIMEOUT);
                reqResults.add(resultDto);
            } catch (IOException e) {
                log.info("Department Scrap all IOException: {}", e.getMessage());
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
            }
        }

        return reqResults;
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

    private int getDeptInfoSize(DeptInfo deptInfo) {
        return deptInfo.getNoticeScrapInfo().getBoardSeqs().size();
    }

    private ScrapingResultDto getScrapingResultDto(int index, DeptInfo deptInfo, int totalNoticeSize, int timeout) throws IOException {
        String requestUrl = deptInfo.createRequestUrl(index, totalNoticeSize, PAGE_NUM);
        String viewUrl = deptInfo.createViewUrl(index);

        Document document = jsoupClient.get(requestUrl, timeout);

        return new ScrapingResultDto(document, viewUrl);
    }
}

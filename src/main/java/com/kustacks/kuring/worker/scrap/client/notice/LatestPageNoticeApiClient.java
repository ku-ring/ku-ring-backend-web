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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LatestPageNoticeApiClient implements NoticeApiClient<ScrapingResultDto, DeptInfo> {

    private static final int START_PAGE_NUM = 1; // page는 인자가 1부터 시작
    private static final int ROW_NUMBERS_PER_PAGE = 100;
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

    /**
     * Scrapes every listing page for the specified department, merges the rows into one HTML document, and produces a single result referencing the department's view URL.
     *
     * The method returns an empty list when scraping cannot complete (for example, network errors or missing listing content); it throws InternalLogicException when page parsing fails in a way that indicates malformed or unexpected HTML structure.
     *
     * @param deptInfo information about the department whose notices will be scraped
     * @return a list containing one ScrapingResultDto with the merged listing document and the department view URL, or an empty list if scraping did not complete
     * @throws InternalLogicException if parsing the pages fails (e.g., unexpected/malformed HTML structure)
     */
    @Override
    public List<ScrapingResultDto> requestAll(DeptInfo deptInfo) throws InternalLogicException {
        try {
            String totalUrl = buildUrlForTotalNoticeCount(deptInfo);
            int totalNoticeSize = getTotalNoticeSize(totalUrl);
            int totalPages = (int) Math.ceil((double) totalNoticeSize / ROW_NUMBERS_PER_PAGE);

            String viewUrl = deptInfo.createUndergraduateViewUrl();

            String firstUrl = deptInfo.createUndergraduateRequestUrl(START_PAGE_NUM, ROW_NUMBERS_PER_PAGE);
            log.info("[SCRAP] dept={} basePage=1/{} url={}", deptInfo.getDeptName(), totalPages, firstUrl);

            Document baseDoc = jsoupClient.get(firstUrl, LATEST_SCRAP_TIMEOUT);

            Element baseTbody = baseDoc.selectFirst(".board-table > tbody");
            if (baseTbody == null) {
                log.warn("[SCRAP] dept={} base page has no tbody, title={}", deptInfo.getDeptName(), baseDoc.title());
                return Collections.emptyList();
            }

            for(int page = START_PAGE_NUM + 1; page <= totalPages; page++){
                String pageUrl = deptInfo.createUndergraduateRequestUrl(page, ROW_NUMBERS_PER_PAGE);
                log.info("[SCRAP] dept={} page={}/{} url={}", deptInfo.getDeptName(), page, totalPages, pageUrl);

                Document doc = jsoupClient.get(pageUrl, LATEST_SCRAP_TIMEOUT);
                Element tbody = doc.selectFirst(".board-table > tbody");
                if (tbody == null) {
                    log.warn("[SCRAP] dept={} page={} no tbody, title={}", deptInfo.getDeptName(), page, doc.title());
                    break;
                }

                Elements trs = tbody.select("tr");
                int rows = trs.size();

                if (rows == 0) break;

                for (Element tr : trs) {
                    baseTbody.appendChild(tr.clone());
                }
            }

            int mergedRows = baseDoc.select(".board-table > tbody > tr").size();
            log.info("[SCRAP] dept={} merged total rows={}", deptInfo.getDeptName(), mergedRows);

            return List.of(new ScrapingResultDto(baseDoc, viewUrl));

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

    private String buildUrlForTotalNoticeCount(DeptInfo deptInfo) {
        return deptInfo.createUndergraduateRequestUrl(1, 1);
    }

    /**
     * Fetches the first page of notices for the given department and returns a scraping result.
     *
     * @param deptInfo department metadata used to build the request and view URLs
     * @param rowSize number of rows to request per page
     * @param timeout per-request timeout in milliseconds
     * @return a ScrapingResultDto containing the fetched HTML Document and the department's view URL
     * @throws IOException if the HTTP request fails
     */
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
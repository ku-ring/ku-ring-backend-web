package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.JsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LatestPageNoticeApiClient implements NoticeApiClient<ScrapingResultDto, DeptInfo> {

    private static final int START_PAGE_NUM = 1; // page는 인자가 1부터 시작
    private static final int ROW_NUMBERS_PER_PAGE = 20;
    private static final int LATEST_SCRAP_TIMEOUT = 30000; // 30초
    private static final int LATEST_SCRAP_ALL_TIMEOUT = 120000; // 2분

    private final JsoupClient jsoupClient;

    public LatestPageNoticeApiClient(JsoupClient normalJsoupClient) {
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
            String url = buildUrlForTotalNoticeCount(deptInfo);
            int totalNoticeSize = getTotalNoticeSize(url);

            ScrapingResultDto resultDto = getScrapingResultDto(deptInfo, totalNoticeSize, LATEST_SCRAP_ALL_TIMEOUT);
            return List.of(resultDto);
        } catch (IOException e) {
            log.info("Department Scrap all IOException: {}", e.getMessage());
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }

        return Collections.emptyList();
    }

    private String buildUrlForTotalNoticeCount(DeptInfo deptInfo) {
        return deptInfo.createRequestUrl(1, 1);
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

    private ScrapingResultDto getScrapingResultDto(DeptInfo deptInfo, int rowSize, int timeout) throws IOException {
        String requestUrl = deptInfo.createRequestUrl(START_PAGE_NUM, rowSize);

        String viewUrl = deptInfo.createViewUrl();

        long startTime = 0L;
        Document document = jsoupClient.get(requestUrl, timeout);
        long endTime = System.currentTimeMillis();
        log.info("[학과] 업데이트 시작으로부터 {}millis 만큼 지남", endTime - startTime);

        return new ScrapingResultDto(document, viewUrl);
    }
}

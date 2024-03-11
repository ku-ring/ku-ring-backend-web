package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.JsoupClient;
import com.kustacks.kuring.worker.scrap.noticeinfo.KuisHomepageNoticeInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class KuisHomepageNoticeApiClient implements NoticeApiClient<ScrapingResultDto, KuisHomepageNoticeInfo> {

    private static final int START_PAGE_NUM = 1; // page는 인자가 1부터 시작
    private static final int ROW_NUMBERS_PER_PAGE = 20;
    private static final int LATEST_SCRAP_TIMEOUT = 2000; // 2초
    private static final int LATEST_SCRAP_ALL_TIMEOUT = 10000; // 10초
    private static final int TOTAL_KUIS_NOTICES_COUNT = 650;

    private final JsoupClient jsoupClient;

    public KuisHomepageNoticeApiClient(JsoupClient normalJsoupClient) {
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public List<ScrapingResultDto> request(KuisHomepageNoticeInfo kuisHomepageNoticeInfo) throws InternalLogicException {
        try {
            ScrapingResultDto resultDto = getScrapingResultDto(kuisHomepageNoticeInfo, ROW_NUMBERS_PER_PAGE, LATEST_SCRAP_TIMEOUT);
            return List.of(resultDto);
        } catch (IOException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }
    }

    @Override
    public List<ScrapingResultDto> requestAll(KuisHomepageNoticeInfo kuisHomepageNoticeInfo) throws InternalLogicException {
        try {
            String url = buildUrlForTotalNoticeCount(kuisHomepageNoticeInfo);
            int totalNoticeSize = getTotalNoticeSize(url);

            ScrapingResultDto resultDto = getScrapingResultDto(kuisHomepageNoticeInfo, totalNoticeSize, LATEST_SCRAP_ALL_TIMEOUT);
            return List.of(resultDto);
        } catch (IOException e) {
            log.info("Department Scrap all IOException: {}", e.getMessage());
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }

        return Collections.emptyList();
    }

    public int getTotalNoticeSize(String url) throws IOException, IndexOutOfBoundsException, NullPointerException {
        Document document = jsoupClient.get(url, LATEST_SCRAP_TIMEOUT);

        Element totalNoticeSizeElement = document.selectFirst(".util-search strong");
        if (totalNoticeSizeElement == null) {
            totalNoticeSizeElement = document.selectFirst(".total_count");
        }

        if(totalNoticeSizeElement == null) { // 총 공지 개수가 없는 경우 650개로 가정
            return TOTAL_KUIS_NOTICES_COUNT;
        }

        return Math.min(Integer.parseInt(totalNoticeSizeElement.ownText()), TOTAL_KUIS_NOTICES_COUNT);
    }

    private String buildUrlForTotalNoticeCount(KuisHomepageNoticeInfo kuisHomepageNoticeInfo) {
        return kuisHomepageNoticeInfo.createRequestUrl(1, 1);
    }

    private ScrapingResultDto getScrapingResultDto(KuisHomepageNoticeInfo kuisHomepageNoticeInfo, int rowSize, int timeout) throws IOException {
        String requestUrl = kuisHomepageNoticeInfo.createRequestUrl(START_PAGE_NUM, rowSize);
        String viewUrl = kuisHomepageNoticeInfo.createViewUrl();

        StopWatch stopWatch = new StopWatch(kuisHomepageNoticeInfo.getCategoryName() + "Request");
        stopWatch.start();

        Document document = jsoupClient.get(requestUrl, timeout);

        stopWatch.stop();
        log.info("[{}] takes {}millis to respond", kuisHomepageNoticeInfo.getCategoryName(), stopWatch.getTotalTimeMillis());

        return new ScrapingResultDto(document, viewUrl);
    }
}

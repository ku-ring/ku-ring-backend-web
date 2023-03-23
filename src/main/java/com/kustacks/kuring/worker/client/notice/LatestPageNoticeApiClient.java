package com.kustacks.kuring.worker.client.notice;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.client.staff.JsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.kustacks.kuring.worker.client.staff.StaffApiClient.SCRAP_TIMEOUT;

@Slf4j
@Component
public class LatestPageNoticeApiClient implements NoticeApiClient<ScrapingResultDto, DeptInfo> {

    private static final int PAGE_NUM = 1;    // recentPage는 pageNum 인자가 1부터 시작
    private static final int ARTICLE_NUMBERS_PER_PAGE = 12;

    private final JsoupClient jsoupClient;
    private final LatestPageProperties latestPageProperties;

    public LatestPageNoticeApiClient(JsoupClient normalJsoupClient, LatestPageProperties latestPageProperties) {
        this.jsoupClient = normalJsoupClient;
        this.latestPageProperties = latestPageProperties;
    }

    @Override
    public List<ScrapingResultDto> request(DeptInfo deptInfo) throws InternalLogicException {
        List<String> boardSeqs = deptInfo.getNoticeScrapInfo().getBoardSeqs();
        List<String> menuSeqs = deptInfo.getNoticeScrapInfo().getMenuSeqs();
        String siteId = deptInfo.getNoticeScrapInfo().getSiteId();

        List<ScrapingResultDto> reqResults = new LinkedList<>();
        for (int i = 0; i < boardSeqs.size(); i++) {
            try {
                String boardSeq = boardSeqs.get(i);
                String menuSeq = menuSeqs.get(i);

                ScrapingResultDto resultDto = getScrapingResultDto(boardSeq, menuSeq, siteId, ARTICLE_NUMBERS_PER_PAGE);
                reqResults.add(resultDto);
            } catch (IOException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
            }
        }

        return reqResults;
    }

    public List<ScrapingResultDto> requestAllPage(DeptInfo deptInfo) throws InternalLogicException {
        List<String> boardSeqs = deptInfo.getNoticeScrapInfo().getBoardSeqs();
        List<String> menuSeqs = deptInfo.getNoticeScrapInfo().getMenuSeqs();
        String siteId = deptInfo.getNoticeScrapInfo().getSiteId();

        List<ScrapingResultDto> reqResults = new LinkedList<>();
        for (int i = 0; i < boardSeqs.size(); i++) {
            try {
                String boardSeq = boardSeqs.get(i);
                String menuSeq = menuSeqs.get(i);
                int totalNoticeSize = getTotalNoticeSize(siteId, boardSeq, menuSeq);

                ScrapingResultDto resultDto = getScrapingResultDto(boardSeq, menuSeq, siteId, totalNoticeSize);
                reqResults.add(resultDto);
            } catch (IOException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
            }
        }

        return reqResults;
    }

    private ScrapingResultDto getScrapingResultDto(String boardSeq, String menuSeq, String siteId, int totalNoticeSize) throws IOException {
        String requestUrl = createRequestUrl(siteId, boardSeq, menuSeq, totalNoticeSize, PAGE_NUM); // Html 요청
        String viewUrl = createViewUrl(siteId, boardSeq, menuSeq);

        Document document = jsoupClient.get(requestUrl, SCRAP_TIMEOUT);

        return new ScrapingResultDto(document, viewUrl);
    }

    private int getTotalNoticeSize(String siteId, String boardSeq, String menuSeq)
            throws IOException, IndexOutOfBoundsException, NullPointerException {

        String url = createRequestUrl(siteId, boardSeq, menuSeq, 1, 1);
        Document document = jsoupClient.get(url, SCRAP_TIMEOUT);

        Element totalNoticeSizeElement = document.selectFirst(".pl15 > strong");
        if (totalNoticeSizeElement == null) {
            totalNoticeSizeElement = document.selectFirst(".total_count");
        }

        return Integer.parseInt(totalNoticeSizeElement.ownText());
    }

    private String createRequestUrl(String siteId, String boardSeq, String menuSeq, int curPage, int pageNum) {
        return UriComponentsBuilder.fromUriString(latestPageProperties.getListUrl())
                .queryParam("siteId", siteId)
                .queryParam("boardSeq", boardSeq)
                .queryParam("menuSeq", menuSeq)
                .queryParam("curPage", curPage)
                .queryParam("pageNum", pageNum)
                .build()
                .toUriString();
    }

    private String createViewUrl(String siteId, String boardSeq, String menuSeq) {
        return UriComponentsBuilder.fromUriString(latestPageProperties.getViewUrl())
                .queryParam("siteId", siteId)
                .queryParam("boardSeq", boardSeq)
                .queryParam("menuSeq", menuSeq)
                .queryParam("seq", "")
                .build()
                .toUriString();
    }
}

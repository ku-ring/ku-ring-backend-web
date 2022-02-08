package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.api.staff.JsoupClient;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class RecentPageNoticeAPIClient implements NoticeAPIClient<Document, DeptInfo> {

    @Value("${notice.recent-url}")
    private String baseUrl;

    private final JsoupClient jsoupClient;

    public RecentPageNoticeAPIClient(JsoupClient normalJsoupClient) {
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public List<Document> request(DeptInfo deptInfo) throws InternalLogicException {
        List<Document> documents = new LinkedList<>();
        List<String> boardSeqs = deptInfo.getNoticeScrapInfo().getBoardSeqs();
        List<String> menuSeqs = deptInfo.getNoticeScrapInfo().getMenuSeqs();
        String siteId = deptInfo.getNoticeScrapInfo().getSiteId();

        for(int i=0; i<boardSeqs.size(); ++i) {
            String boardSeq = boardSeqs.get(i);
            String menuSeq = menuSeqs.get(i);

            int pageNum = 1; // recentPage는 pageNum 인자가 1부터 시작. leagcy는 p가 0부터 시작이었음
            int curPage = 1;

            for(int j=0; j<2; ++j) {
                UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                        .queryParam("siteId", siteId)
                        .queryParam("boardSeq", boardSeq)
                        .queryParam("menuSeq", menuSeq)
                        .queryParam("curPage", curPage)
                        .queryParam("pageNum", pageNum);
                String url = urlBuilder.build().toUriString();

                try {
                    Document document = jsoupClient.get(url, SCRAP_TIMEOUT);

                    if(curPage == 1) {
                        curPage = getTotalNoticeSize(document);
                        log.info("curPage = {}", curPage);
                    } else {
                        documents.add(document);
                    }
                } catch(IOException e) {
                    throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
                } catch(NullPointerException | IndexOutOfBoundsException e) {
                    throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
                }
            }
        }

        return documents;
    }

    private int getTotalNoticeSize(Document document) throws IndexOutOfBoundsException, NullPointerException {
        Element totalNoticeSizeElement = document.selectFirst(".pl15 > strong");
        if(totalNoticeSizeElement == null) {
            totalNoticeSizeElement = document.selectFirst(".total_count");
        }
        String totalNoticeSize = totalNoticeSizeElement.ownText();
        return Integer.parseInt(totalNoticeSize);
    }
}

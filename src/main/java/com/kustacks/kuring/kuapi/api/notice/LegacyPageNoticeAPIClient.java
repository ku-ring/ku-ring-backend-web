package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.api.staff.JsoupClient;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.kustacks.kuring.kuapi.api.staff.StaffAPIClient.SCRAP_TIMEOUT;

@Component
public class LegacyPageNoticeAPIClient implements NoticeAPIClient<Document, DeptInfo> {

    @Value("${notice.legacy-url}")
    private String baseUrl;

    private final JsoupClient jsoupClient;

    private final int UNKNOWN_PAGE_NUM = 100000;

    public LegacyPageNoticeAPIClient(JsoupClient normalJsoupClient) {
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public List<Document> request(DeptInfo deptInfo) throws InternalLogicException {
        List<Document> documents = new LinkedList<>();

        for (String forumId : deptInfo.getNoticeScrapInfo().getForumIds()) {
            int totalPageNum = UNKNOWN_PAGE_NUM;
            int pageNum = 0;
            do {
                UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                        .queryParam("forum", forumId)
                        .queryParam("p", pageNum);
                String url = urlBuilder.toUriString();

                try {
                    Document document = jsoupClient.get(url, SCRAP_TIMEOUT);
                    documents.add(document);

                    if(totalPageNum == UNKNOWN_PAGE_NUM) {
                        totalPageNum = getTotalPageNum(document);
                    }

                    ++pageNum;
                } catch(IOException e) {
                    throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
                } catch(IndexOutOfBoundsException e) {
                    throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
                }
            } while(pageNum <= totalPageNum);
        }

        return documents;
    }

    private int getTotalPageNum(Document document) {
        Elements idxes = document.getElementsByAttributeValueContaining("src", "btn_list_next02.gif");
        String hrefValue = idxes.get(0).parent().attr("href");

        UriComponents hrefUri = UriComponentsBuilder.fromUriString(hrefValue).build();
        MultiValueMap<String, String> queryParams = hrefUri.getQueryParams();
        List<String> p = queryParams.get("p");

        return Integer.parseInt(p.get(0));
    }
}

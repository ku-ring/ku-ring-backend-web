package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.api.staff.JsoupClient;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class RealEstateNoticeAPIClient implements NoticeAPIClient<Document, DeptInfo> {

    @Value("${notice.real-estate-url}")
    private String baseUrl;

    private final JsoupClient jsoupClient;
    private final int UNKNOWN_PAGE_NUM = 100000;

    public RealEstateNoticeAPIClient(JsoupClient proxyJsoupClient) {
        this.jsoupClient = proxyJsoupClient;
    }

    @Override
    public List<Document> request(DeptInfo deptInfo) throws InternalLogicException {
        List<Document> documents = new LinkedList<>();

        int totalPageNum = UNKNOWN_PAGE_NUM;
        int pageNum = 1;
        do {
            try {
                UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                        .queryParam("sca", "학부")
                        .queryParam("page", pageNum);
                String url = urlBuilder.toUriString();

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

        return documents;
    }

    private int getTotalPageNum(Document document) {
        Element lastPageNumElement = document.select(".paging > ul > li").last();
        Element lastPageBtnElement = lastPageNumElement.getElementsByTag("a").get(1);
        String hrefValue = lastPageBtnElement.attr("href");

        UriComponents hrefUri = UriComponentsBuilder.fromUriString(hrefValue).build();
        MultiValueMap<String, String> queryParams = hrefUri.getQueryParams();
        List<String> page = queryParams.get("page");
        return Integer.parseInt(page.get(0));
    }
}

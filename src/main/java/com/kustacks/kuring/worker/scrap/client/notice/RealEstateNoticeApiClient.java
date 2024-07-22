package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.ProxyJsoupClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.RealEstateNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class RealEstateNoticeApiClient implements NoticeApiClient<ScrapingResultDto, DeptInfo> {

    private static final int PAGE_NUM = 1;
    private static final int UNKNOWN_PAGE_NUM = -1;
    private static final int ESTATE_SCRAP_TIMEOUT = 300000;

    private final ProxyJsoupClient jsoupClient;
    private final RealEstateNoticeProperties realEstateNoticeProperties;

    public RealEstateNoticeApiClient(ProxyJsoupClient proxyJsoupClient, RealEstateNoticeProperties realEstateNoticeProperties) {
        this.jsoupClient = proxyJsoupClient;
        this.realEstateNoticeProperties = realEstateNoticeProperties;
    }

    @Override
    public List<ScrapingResultDto> request(DeptInfo deptInfo) throws InternalLogicException {
        try {
            String sca = "학부";
            String viewUrl = createViewUrl(sca);
            String reqUrl = createRequestUrl(sca, PAGE_NUM);

            Document document = jsoupClient.get(reqUrl, ESTATE_SCRAP_TIMEOUT);

            return List.of(new ScrapingResultDto(document, viewUrl));
        } catch (IOException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
        } catch (IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }
    }

    @Override
    public List<ScrapingResultDto> requestAll(DeptInfo deptInfo) throws InternalLogicException {
        int totalPageNum = UNKNOWN_PAGE_NUM;
        int nowPageNum = 1;
        String sca = "학부";
        String viewUrl = createViewUrl(sca);

        List<ScrapingResultDto> reqResults = new LinkedList<>();
        do {
            try {
                String requestUrl = createRequestUrl(sca, nowPageNum);

                Document document = jsoupClient.get(requestUrl, ESTATE_SCRAP_TIMEOUT);
                reqResults.add(new ScrapingResultDto(document, viewUrl));

                if (totalPageNum == UNKNOWN_PAGE_NUM) {
                    totalPageNum = getTotalPageNum(document);
                }

                nowPageNum++;
            } catch (IOException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e);
            } catch (IndexOutOfBoundsException e) {
                throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
            }
        } while (nowPageNum <= totalPageNum);

        return reqResults;
    }

    @Override
    public ScrapingResultDto requestSinglePageWithUrl(DeptInfo noticeInfo, String url) {
        throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE);
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

    private String createRequestUrl(String sca, int pageNum) {
        return UriComponentsBuilder
                .fromUriString(realEstateNoticeProperties.listUrl())
                .queryParam("sca", sca)
                .queryParam("page", pageNum)
                .build()
                .toUriString();
    }

    private String createViewUrl(String sca) {
        return UriComponentsBuilder
                .fromUriString(realEstateNoticeProperties.viewUrl())
                .queryParam("sca", sca)
                .queryParam("wr_id", "")
                .build()
                .toUriString();
    }
}

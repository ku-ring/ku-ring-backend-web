package com.kustacks.kuring.worker.scrap.noticeinfo;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisHomepageNoticeProperties;
import com.kustacks.kuring.worker.parser.notice.NoticeHtmlParserTemplate;
import com.kustacks.kuring.worker.parser.notice.RowsDto;
import lombok.Getter;
import org.jsoup.nodes.Document;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Getter
public class KuisHomepageNoticeInfo {

    protected NoticeApiClient<ScrapingResultDto, KuisHomepageNoticeInfo> noticeApiClient;
    protected KuisHomepageNoticeProperties kuisHomepageNoticeProperties;
    protected NoticeHtmlParserTemplate htmlParser;
    protected CategoryName categoryName;
    protected String category = "konkuk";
    protected Integer siteId;

    public List<ScrapingResultDto> scrapLatestPageHtml() {
        return noticeApiClient.request(this);
    }

    public List<ScrapingResultDto> scrapAllPageHtml() {
        return noticeApiClient.requestAll(this);
    }

    public RowsDto parse(Document document) {
        return htmlParser.parse(document);
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public String createRequestUrl(int page, int row) {
        return UriComponentsBuilder
                .fromUriString(kuisHomepageNoticeProperties.listUrl())
                .queryParam("page", page)
                .queryParam("row", row)
                .buildAndExpand(category, siteId)
                .toUriString();
    }

    public String createViewUrl() {
        return kuisHomepageNoticeProperties.viewUrl()
                .replace("{category}", category)
                .replace("{siteId}", String.valueOf(siteId));
    }

    @Override
    public String toString() {
        return categoryName.getName();
    }
}


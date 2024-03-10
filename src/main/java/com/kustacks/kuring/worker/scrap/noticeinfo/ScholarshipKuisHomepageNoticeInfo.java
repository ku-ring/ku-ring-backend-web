package com.kustacks.kuring.worker.scrap.noticeinfo;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisHomepageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisHomepageNoticeProperties;
import com.kustacks.kuring.worker.parser.notice.KuisHomepageNoticeHtmlParser;
import org.springframework.stereotype.Component;

@Component
public class ScholarshipKuisHomepageNoticeInfo extends KuisHomepageNoticeInfo {
    public ScholarshipKuisHomepageNoticeInfo(
            KuisHomepageNoticeApiClient kuisHomepageNoticeApiClient,
            KuisHomepageNoticeHtmlParser kuisHomepageNoticeHtmlParser,
            KuisHomepageNoticeProperties kuisHomepageNoticeProperties
    ) {
        this.noticeApiClient = kuisHomepageNoticeApiClient;
        this.htmlParser = kuisHomepageNoticeHtmlParser;
        this.kuisHomepageNoticeProperties = kuisHomepageNoticeProperties;
        this.siteId = 235;
        this.categoryName = CategoryName.SCHOLARSHIP;
    }
}

package com.kustacks.kuring.worker.scrap.noticeinfo;

import com.kustacks.kuring.worker.scrap.client.notice.KuisHomepageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisHomepageNoticeProperties;
import com.kustacks.kuring.worker.scrap.parser.notice.KuisHomepageNoticeHtmlParser;
import org.springframework.stereotype.Component;

import static com.kustacks.kuring.notice.domain.CategoryName.EMPLOYMENT;

@Component
public class EmploymentKuisHomepageNoticeInfo extends KuisHomepageNoticeInfo {
    public EmploymentKuisHomepageNoticeInfo(
            KuisHomepageNoticeApiClient kuisHomepageNoticeApiClient,
            KuisHomepageNoticeHtmlParser kuisHomepageNoticeHtmlParser,
            KuisHomepageNoticeProperties kuisHomepageNoticeProperties
    ) {
        this.noticeApiClient = kuisHomepageNoticeApiClient;
        this.htmlParser = kuisHomepageNoticeHtmlParser;
        this.kuisHomepageNoticeProperties = kuisHomepageNoticeProperties;
        this.category = "job";
        this.siteId = 4083;
        this.categoryName = EMPLOYMENT;
    }
}

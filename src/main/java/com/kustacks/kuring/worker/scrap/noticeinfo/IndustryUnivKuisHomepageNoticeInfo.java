package com.kustacks.kuring.worker.scrap.noticeinfo;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisHomepageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisHomepageNoticeProperties;
import com.kustacks.kuring.worker.scrap.parser.notice.KuisHomepageNoticeHtmlParser;
import org.springframework.stereotype.Component;

@Component
public class IndustryUnivKuisHomepageNoticeInfo extends KuisHomepageNoticeInfo {
    public IndustryUnivKuisHomepageNoticeInfo(
            KuisHomepageNoticeApiClient kuisHomepageNoticeApiClient,
            KuisHomepageNoticeHtmlParser kuisHomepageNoticeHtmlParser,
            KuisHomepageNoticeProperties kuisHomepageNoticeProperties
    ) {
        this.noticeApiClient = kuisHomepageNoticeApiClient;
        this.htmlParser = kuisHomepageNoticeHtmlParser;
        this.kuisHomepageNoticeProperties = kuisHomepageNoticeProperties;
        this.category = "research";
        this.siteId = 4214;
        this.categoryName = CategoryName.INDUSTRY_UNIVERSITY;
    }
}

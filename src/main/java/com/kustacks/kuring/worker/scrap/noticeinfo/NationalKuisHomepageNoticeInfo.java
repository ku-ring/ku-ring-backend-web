package com.kustacks.kuring.worker.scrap.noticeinfo;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisHomepageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisHomepageNoticeProperties;
import com.kustacks.kuring.worker.scrap.parser.notice.KuisHomepageNoticeHtmlParser;
import org.springframework.stereotype.Component;

@Component
public class NationalKuisHomepageNoticeInfo extends KuisHomepageNoticeInfo {
    public NationalKuisHomepageNoticeInfo(
            KuisHomepageNoticeApiClient kuisHomepageNoticeApiClient,
            KuisHomepageNoticeHtmlParser kuisHomepageNoticeHtmlParser,
            KuisHomepageNoticeProperties kuisHomepageNoticeProperties
    ) {
        this.noticeApiClient = kuisHomepageNoticeApiClient;
        this.htmlParser = kuisHomepageNoticeHtmlParser;
        this.kuisHomepageNoticeProperties = kuisHomepageNoticeProperties;
        this.siteId = 237;
        this.categoryName = CategoryName.NATIONAL;
    }
}

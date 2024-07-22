package com.kustacks.kuring.worker.scrap.noticeinfo;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.KuisHomepageNoticeTextParser;
import com.kustacks.kuring.worker.scrap.client.notice.KuisHomepageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisHomepageNoticeProperties;
import com.kustacks.kuring.worker.parser.notice.KuisHomepageNoticeHtmlParser;
import org.springframework.stereotype.Component;

@Component
public class NormalKuisHomepageNoticeInfo extends KuisHomepageNoticeInfo {
    public NormalKuisHomepageNoticeInfo(
            KuisHomepageNoticeApiClient kuisHomepageNoticeApiClient,
            KuisHomepageNoticeHtmlParser kuisHomepageNoticeHtmlParser,
            KuisHomepageNoticeTextParser kuisHomepageNoticeTextParser,
            KuisHomepageNoticeProperties kuisHomepageNoticeProperties
    ) {
        this.noticeApiClient = kuisHomepageNoticeApiClient;
        this.htmlParser = kuisHomepageNoticeHtmlParser;
        this.textParser = kuisHomepageNoticeTextParser;
        this.kuisHomepageNoticeProperties = kuisHomepageNoticeProperties;
        this.siteId = 240;
        this.categoryName = CategoryName.NORMAL;
    }
}

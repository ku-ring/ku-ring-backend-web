package com.kustacks.kuring.worker.update.notice.dto.request;

import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import org.springframework.stereotype.Component;

@Component
public class NationalKuisNoticeInfo extends KuisNoticeInfo {
    public NationalKuisNoticeInfo(KuisNoticeApiClient kuisNoticeApiClient) {
        super(kuisNoticeApiClient);
        this.v1 = "notice";
        this.v2 = "0000300002";
        this.categoryName = CategoryName.NATIONAL;
    }
}

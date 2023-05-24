package com.kustacks.kuring.worker.update.notice.dto.request;

import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import org.springframework.stereotype.Component;

@Component
public class NormalKuisNoticeInfo extends KuisNoticeInfo {
    public NormalKuisNoticeInfo(KuisNoticeApiClient kuisNoticeApiClient) {
        super(kuisNoticeApiClient);
        this.v1 = "notice";
        this.v2 = "0000300006";
        this.categoryName = CategoryName.NORMAL;
    }
}

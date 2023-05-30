package com.kustacks.kuring.worker.update.notice.dto.request;

import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import org.springframework.stereotype.Component;

@Component
public class ScholarshipKuisNoticeInfo extends KuisNoticeInfo {
    public ScholarshipKuisNoticeInfo(KuisNoticeApiClient kuisNoticeApiClient) {
        super(kuisNoticeApiClient);
        this.v1 = "11688412";
        this.v2 = "";
        this.categoryName = CategoryName.SCHOLARSHIP;
    }
}

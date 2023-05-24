package com.kustacks.kuring.worker.update.notice.dto.request;

import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import org.springframework.stereotype.Component;

@Component
public class IndustryUnivKuisNoticeInfo extends KuisNoticeInfo {
    public IndustryUnivKuisNoticeInfo(KuisNoticeApiClient kuisNoticeApiClient) {
        super(kuisNoticeApiClient);
        this.v1 = "65659";
        this.v2 = "";
        this.categoryName = CategoryName.INDUSTRY_UNIVERSITY;
    }
}

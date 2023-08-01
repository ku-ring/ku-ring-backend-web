package com.kustacks.kuring.worker.update.notice.dto.request;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import org.springframework.stereotype.Component;

@Component
public class EmploymentKuisNoticeInfo extends KuisNoticeInfo {
    public EmploymentKuisNoticeInfo(KuisNoticeApiClient kuisNoticeApiClient) {
        super(kuisNoticeApiClient);
        this.v1 = "11731332";
        this.v2 = "";
        this.categoryName = CategoryName.EMPLOYMENT;
    }
}

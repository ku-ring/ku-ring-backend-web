package com.kustacks.kuring.worker.update.notice.dto.request;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import org.springframework.stereotype.Component;

@Component
public class StudentKuisNoticeInfo extends KuisNoticeInfo {
    public StudentKuisNoticeInfo(KuisNoticeApiClient kuisNoticeApiClient) {
        super(kuisNoticeApiClient);
        this.v1 = "notice";
        this.v2 = "0000300003";
        this.categoryName = CategoryName.STUDENT;
    }
}

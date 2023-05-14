package com.kustacks.kuring.worker.update.notice.dto.request;

import org.springframework.stereotype.Component;

@Component
public class StudentKuisNoticeRequestBody extends KuisNoticeRequestBody {
    public StudentKuisNoticeRequestBody() {
        super("notice", "0000300003");
    }
}

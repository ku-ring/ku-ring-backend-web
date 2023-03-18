package com.kustacks.kuring.worker.notice.dto.request;

import org.springframework.stereotype.Component;

@Component
public class NationalKuisNoticeRequestBody extends KuisNoticeRequestBody {
    public NationalKuisNoticeRequestBody() {
        super("notice", "0000300002");
    }
}

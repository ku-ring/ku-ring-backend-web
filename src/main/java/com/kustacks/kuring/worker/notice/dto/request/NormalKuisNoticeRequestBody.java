package com.kustacks.kuring.worker.notice.dto.request;

import org.springframework.stereotype.Component;

@Component
public class NormalKuisNoticeRequestBody extends KuisNoticeRequestBody {
    public NormalKuisNoticeRequestBody() {
        super("notice", "0000300006");
    }
}

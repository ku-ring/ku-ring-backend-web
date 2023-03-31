package com.kustacks.kuring.worker.update.notice.dto.request;

import org.springframework.stereotype.Component;

@Component
public class BachelorKuisNoticeRequestBody extends KuisNoticeRequestBody {
    public BachelorKuisNoticeRequestBody() {
        super("notice", "0000300001");
    }
}

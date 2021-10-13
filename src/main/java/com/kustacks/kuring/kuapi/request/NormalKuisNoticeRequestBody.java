package com.kustacks.kuring.kuapi.request;

import org.springframework.stereotype.Component;

@Component
public class NormalKuisNoticeRequestBody extends KuisNoticeRequestBody {
    public NormalKuisNoticeRequestBody() {
        super("notice", "0000300006");
    }
}

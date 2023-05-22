package com.kustacks.kuring.worker.scrap.client.notice.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "notice.kuis")
public class KuisNoticeProperties {

    private final String requestUrl;
    private final String refererUrl;
}

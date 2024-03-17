package com.kustacks.kuring.worker.scrap.client.notice.property;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "notice.kuis")
public record KuisNoticeProperties(
        String requestUrl,
        String refererUrl
) {
}

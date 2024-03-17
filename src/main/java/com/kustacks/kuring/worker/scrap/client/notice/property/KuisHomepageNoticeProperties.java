package com.kustacks.kuring.worker.scrap.client.notice.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notice.homepage")
public record KuisHomepageNoticeProperties(
        String listUrl,
        String viewUrl
) {
}

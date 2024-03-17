package com.kustacks.kuring.worker.scrap.client.notice.property;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "notice.recent")
public record LatestPageNoticeProperties(
        String listUrl,
        String viewUrl
) {
}

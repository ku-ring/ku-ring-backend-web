package com.kustacks.kuring.worker.scrap.client.notice.property;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "notice.real-estate")
public record RealEstateNoticeProperties(
        String listUrl,
        String viewUrl
) {
}

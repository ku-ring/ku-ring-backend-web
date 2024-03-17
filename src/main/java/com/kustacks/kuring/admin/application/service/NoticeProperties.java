package com.kustacks.kuring.admin.application.service;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "notice")
public record NoticeProperties(
        String normalBaseUrl,
        String libraryBaseUrl
) {
}

package com.kustacks.kuring.admin.application.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "notice")
public class NoticeProperties {

    private final String normalBaseUrl;
    private final String libraryBaseUrl;
}

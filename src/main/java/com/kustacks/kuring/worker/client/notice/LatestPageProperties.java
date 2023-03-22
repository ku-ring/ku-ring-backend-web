package com.kustacks.kuring.worker.client.notice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "notice")
@ConstructorBinding
public class LatestPageProperties {

    private final String recentListUrl;

    private final String recentViewUrl;
}

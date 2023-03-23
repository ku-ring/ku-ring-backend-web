package com.kustacks.kuring.worker.client.notice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "notice.recent")
public class LatestPageProperties {

    private final String listUrl;

    private final String viewUrl;
}

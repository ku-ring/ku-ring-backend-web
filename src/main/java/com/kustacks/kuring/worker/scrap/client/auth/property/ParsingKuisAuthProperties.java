package com.kustacks.kuring.worker.scrap.client.auth.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "auth")
public class ParsingKuisAuthProperties {

    private String apiSkeletonProducerUrl;
    private String referer;
    private String loginUrl;
    private String userAgent;
    private String session;
}

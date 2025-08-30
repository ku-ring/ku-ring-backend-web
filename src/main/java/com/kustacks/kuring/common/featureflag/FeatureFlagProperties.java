package com.kustacks.kuring.common.featureflag;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.appconfig")
public record FeatureFlagProperties(
        String application,
        String environment,
        String profile,
        String region
) {
}

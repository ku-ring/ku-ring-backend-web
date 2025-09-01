package com.kustacks.kuring.common.featureflag;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "aws.appconfig")
public record FeatureFlagProperties(
        @NotBlank String application,
        @NotBlank String environment,
        @NotBlank String profile,
        @NotBlank String region
) {
}

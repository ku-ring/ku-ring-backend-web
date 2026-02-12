package com.kustacks.kuring.common.properties;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "cloud.storage")
public record CloudStorageProperties(
        @NotBlank String region,
        String endpoint, // OCI인 경우에만 사용.
        @NotBlank String bucket,
        @NotNull Credentials credentials
) {
    public record Credentials(
            @NotBlank String accessKey,
            @NotBlank String secretKey
    ) {

    }
}
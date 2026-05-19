package com.kustacks.kuring.common.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@Validated
@Profile("prod")
@ConfigurationProperties(prefix = "cloud.storage.aws")
public record AwsS3Properties(
        /**
         * AWS S3 bucket region id. Example: {@code ap-northeast-2}.
         */
        @NotBlank String region,
        /**
         * AWS S3 bucket name used for production file storage.
         */
        @NotBlank String bucket,
        /**
         * Access credentials for the IAM principal allowed to upload, delete, and presign bucket objects.
         */
        @NotNull @Valid Credentials credentials
) {
    public record Credentials(
            /**
             * AWS access key id for the storage IAM principal.
             */
            @NotBlank String accessKey,
            /**
             * AWS secret access key for the storage IAM principal.
             */
            @NotBlank String secretKey
    ) {
    }
}

package com.kustacks.kuring.common.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@Validated
@Profile("dev")
@ConfigurationProperties(prefix = "cloud.storage.oci")
public record OciStorageProperties(
        /**
         * OCI Object Storage region id. Example: {@code ap-chuncheon-1}.
         */
        @NotBlank String region,
        /**
         * OCI bucket name used for development file storage.
         */
        @NotBlank String bucket,
        /**
         * OCI Object Storage namespace that owns the bucket.
         */
        @NotBlank String namespace,
        /**
         * OCI tenancy OCID for API key authentication.
         */
        @NotBlank String tenancyId,
        /**
         * OCI user OCID for API key authentication.
         */
        @NotBlank String userId,
        /**
         * Fingerprint of the OCI API key registered to the user.
         */
        @NotBlank String fingerprint,
        /**
         * PEM encoded private key for OCI API key authentication.
         */
        @NotBlank String privateKey,
        /**
         * Optional passphrase for the private key. Leave blank when the key is not encrypted.
         */
        String passphrase
) {
}

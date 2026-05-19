package com.kustacks.kuring.config;

import com.kustacks.kuring.common.properties.OciStorageProperties;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@EnableConfigurationProperties(OciStorageProperties.class)
public class StorageOciConfig {

    private final OciStorageProperties ociStorageProperties;

    @Bean
    public ObjectStorage oracleObjectStorageClient() {
        SimpleAuthenticationDetailsProvider.SimpleAuthenticationDetailsProviderBuilder authenticationDetailsProviderBuilder =
                SimpleAuthenticationDetailsProvider.builder()
                        .tenantId(ociStorageProperties.tenancyId())
                        .userId(ociStorageProperties.userId())
                        .fingerprint(ociStorageProperties.fingerprint())
                        .privateKeySupplier(() -> new ByteArrayInputStream(ociStorageProperties.privateKey().getBytes(StandardCharsets.UTF_8)))
                        .region(com.oracle.bmc.Region.fromRegionId(ociStorageProperties.region()));

        if (ociStorageProperties.passphrase() != null && !ociStorageProperties.passphrase().isBlank()) {
            authenticationDetailsProviderBuilder.passphraseCharacters(ociStorageProperties.passphrase().toCharArray());
        }

        SimpleAuthenticationDetailsProvider authenticationDetailsProvider = authenticationDetailsProviderBuilder.build();

        return ObjectStorageClient.builder().build(authenticationDetailsProvider);
    }
}

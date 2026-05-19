package com.kustacks.kuring.config;

import com.kustacks.kuring.common.properties.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Profile("prod")
@RequiredArgsConstructor
@EnableConfigurationProperties(AwsS3Properties.class)
public class StorageAwsConfig {

    private final AwsS3Properties awsS3Properties;

    @Bean
    public S3Client awsS3Client() {
        return S3Client.builder()
                .region(Region.of(awsS3Properties.region()))
                .credentialsProvider(staticCredentialsProvider())
                .build();
    }

    @Bean
    public S3Presigner awsS3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsS3Properties.region()))
                .credentialsProvider(staticCredentialsProvider())
                .build();
    }

    private StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                        awsS3Properties.credentials().accessKey(),
                        awsS3Properties.credentials().secretKey()
                )
        );
    }
}

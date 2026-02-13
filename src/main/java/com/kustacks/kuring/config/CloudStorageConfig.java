package com.kustacks.kuring.config;

import com.kustacks.kuring.common.properties.CloudStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Local, Test환경 에서는 사용하지 않는다.
 * Prod, Dev환경에서 사용한다.
 * Prod : AWS S3 서비스 이용
 * Dev : OCI Storage 서비스 이용.
 * 서비스 사용은 S3기술로 호환가능. Presigned 기술도 V4지원.
 */
@Configuration
@RequiredArgsConstructor
public class CloudStorageConfig {

    private final CloudStorageProperties properties;

    @Bean
    @Profile("prod")
    public S3Client awsS3Client() {
        return defaultS3ClientBuilder()
                .build();
    }

    @Bean
    @Profile("prod")
    public S3Presigner awsS3Presigner() {
        return defaultS3PresignerBuilder()
                .build();
    }

    @Bean
    @Profile("dev")
    public S3Client oracleStorageClient() {
        return defaultS3ClientBuilder()
                .endpointOverride(URI.create(properties.endpoint())) //OCI는 엔드포인트를 강제 지정한다.
                .forcePathStyle(true) // 강제로 PathStyle 지정.
                .build();
    }

    @Bean
    @Profile("dev")
    public S3Presigner oracleS3Presigner() {
        return defaultS3PresignerBuilder()
                .endpointOverride(URI.create(properties.endpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    private S3ClientBuilder defaultS3ClientBuilder() {
        return S3Client.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(staticCredentialsProvider());
    }

    private S3Presigner.Builder defaultS3PresignerBuilder() {
        return S3Presigner.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(staticCredentialsProvider());
    }

    private StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.create(properties.credentials().accessKey(), properties.credentials().secretKey()));
    }
}
package com.kustacks.kuring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.config.env.KuringPropertyRestClient;
import com.kustacks.kuring.config.env.RemotePropertyResolver;
import com.kustacks.kuring.config.featureflag.RemoteFeatureFlags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

/**
 * @author JiwooKim (zbqmgldjfh)
 * AWSAppConfig 클라이언트를 생성하고, 스케줄러를 통해 주기적으로 플래그를 갱신합니다.
 */
@Slf4j
@Configuration
@EnableScheduling
public class FeatureFlagConfig {

    @Value("${aws.appconfig.application}")
    private String applicationName;

    @Value("${aws.appconfig.environment}")
    private String environmentName;

    @Value("${aws.appconfig.profile}")
    private String profileName;

    @Value("${aws.appconfig.region}")
    private String region;

    /**
     * AWS AppConfig 데이터 클라이언트 Bean을 생성합니다. (AWS SDK V2)
     *
     * @return AppConfigDataClient 클라이언트 인스턴스
     */
    @Bean
    public AppConfigDataClient appConfigDataClient() {
        return AppConfigDataClient.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * AppConfigFeatureFlagService Bean을 생성합니다.
     * 이 서비스는 기능 플래그의 상태를 조회하고 갱신하는 역할을 합니다.
     *
     * @param appConfigDataClient AppConfigDataClient (AWS SDK V2)
     * @param objectMapper        ObjectMapper
     * @return AppConfigFeatureFlagService 인스턴스
     */
    @Bean
    public RemotePropertyResolver kuringPropertyRestClient(AppConfigDataClient appConfigDataClient, ObjectMapper objectMapper) {
        return new KuringPropertyRestClient(
                appConfigDataClient,
                objectMapper,
                applicationName,
                environmentName,
                profileName
        );
    }

    @Bean
    public RemoteFeatureFlags remoteFeatureFlags(RemotePropertyResolver remotePropertyResolver) {
        return new RemoteFeatureFlags(remotePropertyResolver);
    }
}

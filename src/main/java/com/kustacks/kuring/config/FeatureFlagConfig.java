package com.kustacks.kuring.config;

import com.kustacks.kuring.common.env.RemotePropertyResolver;
import com.kustacks.kuring.common.featureflag.FeatureFlagProperties;
import com.kustacks.kuring.common.featureflag.RemoteFeatureFlags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

/**
 * @author JiwooKim (zbqmgldjfh)
 * AWSAppConfig 클라이언트를 생성하고, 스케줄러를 통해 주기적으로 플래그를 갱신합니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeatureFlagConfig {

    private final FeatureFlagProperties properties;

    /**
     * AWS AppConfig 데이터 클라이언트 Bean을 생성합니다. (AWS SDK V2)
     *
     * @return AppConfigDataClient 클라이언트 인스턴스
     */
    @Bean
    public AppConfigDataClient appConfigDataClient() {
        return AppConfigDataClient.builder()
                .region(Region.of(properties.region()))
                .build();
    }

    @Bean
    public RemoteFeatureFlags remoteFeatureFlags(RemotePropertyResolver remotePropertyResolver) {
        return new RemoteFeatureFlags(remotePropertyResolver);
    }
}

package com.kustacks.kuring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.common.env.KuringPropertyRestClient;
import com.kustacks.kuring.common.env.MockKuringPropertyRestClient;
import com.kustacks.kuring.common.env.RemotePropertyResolver;
import com.kustacks.kuring.common.featureflag.FeatureFlagProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;

@Configuration
@RequiredArgsConstructor
public class RemotePropertyResolverConfig {

    private final FeatureFlagProperties properties;

    /**
     * AppConfigFeatureFlagService Bean을 생성합니다.
     * 이 서비스는 기능 플래그의 상태를 조회하고 갱신하는 역할을 합니다.
     *
     * @param appConfigDataClient AppConfigDataClient (AWS SDK V2)
     * @param objectMapper        ObjectMapper
     * @return AppConfigFeatureFlagService 인스턴스
     */
    @Bean
    @Profile({"local", "prod"})
    public RemotePropertyResolver kuringRemotePropertyResolver(
            AppConfigDataClient appConfigDataClient,
            ObjectMapper objectMapper
    ) {
        return new KuringPropertyRestClient(
                appConfigDataClient,
                objectMapper,
                properties.application(),
                properties.environment(),
                properties.profile()
        );
    }

    @Bean
    @Profile({"test", "dev"})
    public RemotePropertyResolver mockRemotePropertyResolver() {
        return new MockKuringPropertyRestClient();
    }
}

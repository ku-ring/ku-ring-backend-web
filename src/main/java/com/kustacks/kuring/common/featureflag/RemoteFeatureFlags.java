package com.kustacks.kuring.common.featureflag;

import com.kustacks.kuring.common.env.RemotePropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RemoteFeatureFlags implements FeatureFlags, InitializingBean {

    private final RemotePropertyResolver remotePropertyResolver;

    private Map<String, Boolean> properties = new HashMap<>();

    public RemoteFeatureFlags(RemotePropertyResolver remotePropertyResolver) {
        this.remotePropertyResolver = remotePropertyResolver;
    }

    /**
     * 3분마다 원격에서 최신 기능 플래그를 조회하여 로컬 저장소(RemoteKuringFeatureFlags)를 갱신합니다.
     */
    @Scheduled(initialDelayString = "180000", fixedRateString = "180000")
    public void refreshFeatureFlags() {
        log.debug("Attempting to refresh feature flags from AWS AppConfig");
        this.refresh();
    }

    @Override
    public boolean isEnabled(Feature feature) {
        return properties.getOrDefault(feature.value(), false);
    }

    @Override
    public boolean isDisabled(Feature feature) {
        return !this.isEnabled(feature);
    }

    /**
     * RemotePropertyResolver를 통해 최신 피처 플래그 값을 가져와 내부 상태를 갱신합니다.
     */
    public void refresh() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Boolean> newProperties = (Map<String, Boolean>) remotePropertyResolver.getProperties(Map.class);

            if (newProperties == null) {
                log.warn("Failed to load feature-flags from remote property resolver. Keeping existing properties.");
                return;
            }

            log.debug("RemoteKuringFeatureFlags has been refreshed with new properties. {}", newProperties);
            this.properties = newProperties;
        } catch (Exception e) {
            log.error("Error refreshing feature flags. Existing properties will be retained.", e);
        }
    }

    /**
     * Bean 초기화 시점에 remotePropertyResolver로부터 초기 피처 플래그 값을 가져와 설정합니다.
     */
    @Override
    public void afterPropertiesSet() {
        log.info("Initializing RemoteFeatureFlags properties.");
        this.refresh();
    }
}

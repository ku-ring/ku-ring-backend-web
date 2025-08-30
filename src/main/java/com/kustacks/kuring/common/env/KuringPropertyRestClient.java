package com.kustacks.kuring.common.env;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionRequest;
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KuringPropertyRestClient implements RemotePropertyResolver {

    private final AppConfigDataClient appConfigDataClient;
    private final ObjectMapper objectMapper;
    private final String applicationName;
    private final String environmentName;
    private final String profileName;

    private String configurationToken;

    public KuringPropertyRestClient(
            AppConfigDataClient appConfigDataClient,
            ObjectMapper objectMapper,
            String applicationName,
            String environmentName,
            String profileName
    ) {
        this.appConfigDataClient = appConfigDataClient;
        this.objectMapper = objectMapper;
        this.applicationName = applicationName;
        this.environmentName = environmentName;
        this.profileName = profileName;
    }

    /**
     * 원격(AppConfig)에서 최신 구성을 조회해 해당 key의 값을 즉시 반환합니다.
     * 변경이 없으면 null을 반환하며, 이 경우 호출자는 기존 값을 유지해야 합니다.
     */
    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        Map<String, Object> root = fetchLatestConfigAsMap();
        if (root == null) {
            return null;
        }

        Object value = root.get(key);
        if (value == null) {
            return null;
        }

        try {
            if (targetType.isInstance(value)) {
                return targetType.cast(value);
            }
            return objectMapper.convertValue(value, targetType);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to convert property '{}' to type '{}'.", key, targetType.getName(), e);
            return null;
        }
    }

    @Override
    public <T> T getProperties(Class<T> targetType) {
        // root: "{"notice_embedding_1":{"enabled":true), "notice_embedding_2":{"enabled" false}}" 와 같이 응답옴
        Map<String, Object> root = fetchLatestConfigAsMap();
        if (root == null) {
            return null;
        }

        if (Map.class.isAssignableFrom(targetType)) {
            Map<String, Boolean> resultMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : root.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Map) {
                    Map<String, Object> innerMap = (Map<String, Object>) value;
                    Object enabledValue = innerMap.get("enabled");

                    if (enabledValue instanceof Boolean) {
                        resultMap.put(key, (Boolean) enabledValue);
                    }
                }
            }
            return (T) resultMap;
        }

        try {
            return objectMapper.convertValue(root, targetType);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to convert property to type '{}'.", targetType.getName(), e);
            return null;
        }
    }

    private Map<String, Object> fetchLatestConfigAsMap() {
        try {
            if (this.configurationToken == null) {
                sessionInit();
            }

            GetLatestConfigurationRequest configRequest = GetLatestConfigurationRequest.builder()
                    .configurationToken(this.configurationToken)
                    .build();

            GetLatestConfigurationResponse response = appConfigDataClient.getLatestConfiguration(configRequest);
            this.configurationToken = response.nextPollConfigurationToken();

            SdkBytes configuration = response.configuration();
            if (configuration == null || configuration.asByteArray().length == 0) {
                log.info("Configuration has not changed. No update needed.");
                return null;
            }

            String content = configuration.asString(StandardCharsets.UTF_8);
            log.info("New configuration fetched from AppConfig. Content-Type: {}", response.contentType());
            log.debug("New configuration content: {}", content);

            return objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            log.error("Failed to fetch/parse configuration from AppConfig.", e);
            return null;
        }
    }

    private void sessionInit() {
        log.debug("Starting a new configuration session with AWS AppConfig.");
        StartConfigurationSessionRequest sessionRequest = StartConfigurationSessionRequest.builder()
                .applicationIdentifier(applicationName)
                .environmentIdentifier(environmentName)
                .configurationProfileIdentifier(profileName)
                .build();

        StartConfigurationSessionResponse sessionResponse = appConfigDataClient.startConfigurationSession(sessionRequest);
        this.configurationToken = sessionResponse.initialConfigurationToken();
    }
}

package com.kustacks.kuring.config.env;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MockKuringPropertyRestClient implements RemotePropertyResolver {

    private final Map<String, Object> mockProperties;

    public MockKuringPropertyRestClient() {
        this.mockProperties = new HashMap<>();
        initializeMockProperties();
    }

    private void initializeMockProperties() {
        mockProperties.put("update_notice_embedding", Map.of("enabled", true));
        mockProperties.put("update_department_notice", Map.of("enabled", true));
        mockProperties.put("update_kuis_homepage_notice", Map.of("enabled", true));
        mockProperties.put("update_kuis_notice", Map.of("enabled", true));
        mockProperties.put("update_user", Map.of("enabled", true));
        mockProperties.put("update_staff", Map.of("enabled", true));
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        log.debug("MockKuringPropertyRestClient: Getting property '{}' of type '{}'", key, targetType.getName());

        Object value = mockProperties.get(key);
        if (value == null) {
            log.debug("MockKuringPropertyRestClient: Property '{}' not found, returning null", key);
            return null;
        }

        try {
            if (targetType.isInstance(value)) {
                return targetType.cast(value);
            }

            // Map<String, Object>를 Map<String, Boolean>으로 변환하는 경우
            if (Map.class.isAssignableFrom(targetType) && value instanceof Map) {
                Map<String, Object> sourceMap = (Map<String, Object>) value;
                Map<String, Boolean> resultMap = new HashMap<>();

                for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
                    if (entry.getValue() instanceof Boolean) {
                        resultMap.put(entry.getKey(), (Boolean) entry.getValue());
                    }
                }

                if (targetType.isInstance(resultMap)) {
                    return targetType.cast(resultMap);
                }
            }

            log.warn("MockKuringPropertyRestClient: Failed to convert property '{}' to type '{}'", key, targetType.getName());
            return null;
        } catch (Exception e) {
            log.warn("MockKuringPropertyRestClient: Error converting property '{}' to type '{}'", key, targetType.getName(), e);
            return null;
        }
    }

    @Override
    public <T> T getProperties(Class<T> targetType) {
        log.debug("MockKuringPropertyRestClient: Getting all properties of type '{}'", targetType.getName());

        if (Map.class.isAssignableFrom(targetType)) {
            Map<String, Boolean> resultMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : mockProperties.entrySet()) {
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

            return targetType.cast(resultMap);
        }

        try {
            return targetType.cast(mockProperties);
        } catch (Exception e) {
            log.warn("MockKuringPropertyRestClient: Failed to convert properties to type '{}'", targetType.getName(), e);
            return null;
        }
    }

    /**
     * 테스트에서 특정 property 값을 설정할 수 있는 테스트 메서드
     */
    public void setProperty(String key, Object value) {
        mockProperties.put(key, value);
        log.debug("MockKuringPropertyRestClient: Set property '{}' to '{}'", key, value);
    }

    /**
     * 테스트에서 모든 property를 초기화하는 테스트 메서드
     */
    public void resetProperties() {
        mockProperties.clear();
        initializeMockProperties();
        log.debug("MockKuringPropertyRestClient: Reset all properties to default values");
    }
}

package com.kustacks.kuring.config.env;

import com.kustacks.kuring.common.env.MockKuringPropertyRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MockKuringPropertyRestClientTest {

    private MockKuringPropertyRestClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = new MockKuringPropertyRestClient();
    }

    @Test
    @DisplayName("기본 Feature Flag 값을 반환한다")
    void shouldReturnDefaultFeatureFlagValue() {
        // when
        Map<String, Object> result = mockClient.getProperty("update_notice_embedding", Map.class);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.get("enabled")).isEqualTo(true)
        );
    }

    @Test
    @DisplayName("존재하지 않는 키에 대해서는 null을 반환한다")
    void shouldReturnNullForNonExistentKey() {
        // when
        Object result = mockClient.getProperty("non_existent_key", String.class);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("모든 Feature Flag를 Map으로 반환한다")
    void shouldReturnAllFeatureFlagsAsMap() {
        // when
        Map<String, Boolean> result = mockClient.getProperties(Map.class);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).containsKey("update_notice_embedding"),
                () -> assertThat(result).containsKey("update_department_notice"),
                () -> assertThat(result).containsKey("update_kuis_homepage_notice"),
                () -> assertThat(result).containsKey("update_kuis_notice"),
                () -> assertThat(result).containsKey("update_user"),
                () -> assertThat(result).containsKey("update_staff"),
                () -> result.values().forEach(enabled -> assertThat(enabled).isTrue())
        );
    }

    @Test
    @DisplayName("사용자 정의 값을 설정할 수 있다")
    void shouldSetCustomPropertyValue() {
        // given
        String key = "custom_feature";
        Map<String, Object> value = Map.of("enabled", false);

        // when
        mockClient.setProperty(key, value);
        Map<String, Object> result = mockClient.getProperty(key, Map.class);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.get("enabled")).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("모든 속성을 기본값으로 초기화한다")
    void shouldResetAllPropertiesToDefaultValues() {
        // given
        mockClient.setProperty("custom_feature", Map.of("enabled", false));

        // when
        mockClient.resetProperties();
        Map<String, Boolean> result = mockClient.getProperties(Map.class);

        // then
        assertAll(
                () -> assertThat(result).doesNotContainKey("custom_feature"),
                () -> assertThat(result).containsKey("update_notice_embedding"),
                () -> assertThat(result.get("update_notice_embedding")).isTrue()
        );
    }
}

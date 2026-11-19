package com.kustacks.kuring.notice.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("설정 : NoticeWeeklyRankingReportProperties")
class NoticeWeeklyRankingReportPropertiesTest {

    private static final String PREFIX = "notice.weekly-ranking-report";

    @DisplayName("쉼표로 구분한 여러 수신자를 공백과 빈 값을 정리해 목록으로 읽는다")
    @Test
    void bindMultipleRecipients() {
        // when
        NoticeWeeklyRankingReportProperties properties = bind(Map.of(PREFIX + ".recipients", "a@ku-ring.com, b@ku-ring.com,,"));

        // then
        assertThat(properties.recipients()).containsExactly("a@ku-ring.com", "b@ku-ring.com");
        assertThat(properties.hasRecipients()).isTrue();
    }

    @DisplayName("환경변수가 비어 있거나 설정이 없으면 수신자가 없는 것으로 본다")
    @Test
    void bindEmptyRecipients() {
        assertThat(bind(Map.of(PREFIX + ".recipients", "")).hasRecipients()).isFalse();
        assertThat(bind(Map.of()).hasRecipients()).isFalse();
    }

    private static NoticeWeeklyRankingReportProperties bind(Map<String, String> source) {
        return new Binder(new MapConfigurationPropertySource(source))
                .bindOrCreate(PREFIX, NoticeWeeklyRankingReportProperties.class);
    }
}

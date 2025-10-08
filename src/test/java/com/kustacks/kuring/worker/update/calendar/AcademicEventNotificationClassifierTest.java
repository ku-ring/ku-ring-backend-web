package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.Transparent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AcademicEventNotificationClassifier 단위 테스트")
class AcademicEventNotificationClassifierTest {

    @DisplayName("OPAQUE 타입은 알림 활성화")
    @Test
    void proceed_opaque_transparent_returns_true() {
        // given
        Transparent transparent = Transparent.OPAQUE;
        String summary = "일반 학사 일정";

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("TRANSPARENT 타입에서 액션 필요 키워드가 없으면 알림 비활성화")
    @Test
    void proceed_transparent_without_action_keywords_returns_false() {
        // given
        Transparent transparent = Transparent.TRANSPARENT;
        String summary = "일반 개강일";

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("TRANSPARENT 타입에서 액션 필요 키워드가 있으면 알림 활성화")
    @ParameterizedTest
    @ValueSource(strings = {
            "수강신청",
            "수강정정",
            "등록금납입",
            "환불마감",
            "휴학신청",
            "복학신청",
            "재입학접수",
            "졸업논문입력",
            "전과신청",
            "다부전공신청",
            "교직무시험검정원서",
            "성적이의신청",
            "취득학점포기",
            "장학금신청",
            "폐강교과목공지"
    })
    void proceed_transparent_with_action_keywords_returns_true(String summary) {
        // given
        Transparent transparent = Transparent.TRANSPARENT;

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("null 또는 빈 summary 처리")
    @ParameterizedTest
    @CsvSource({
            ",",
            "'',",
            "'   ',"
    })
    void proceed_null_or_empty_summary_with_transparent(String summary) {
        // given
        Transparent transparent = Transparent.TRANSPARENT;

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("액션 필요 키워드 포함 여부 - 대소문자 무관")
    @ParameterizedTest
    @CsvSource({
            "수강신청, true",
            "수강정정 안내, true",
            "등록금 납입, true",
            "환불마감 기한, true",
            "취득학점포기, true",
            "성적이의신청, true",
            "교직무시험검정원서, true",
            "장학금신청기간, true",
            "폐강교과목공지, true",
            "개강, false",
            "방학, false",
            "중간고사, false",
            "기말고사, false",
            "학위수여식, false",
            "예술제, false"
    })
    void proceed_action_keyword_detection_case_insensitive(String summary, boolean expected) {
        // given
        Transparent transparent = Transparent.TRANSPARENT;

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("복합 키워드 테스트")
    @ParameterizedTest
    @ValueSource(strings = {
            "전체학년 수강신청",
            "2024년도 등록금 납입 안내",
            "동계 계절학기 중간고사 및 환불 마감",
            "수강정정 및 초과과목 신청",
            "취득학점포기(3차) 신청",
            "성적이의신청 및 최종성적이의신청",
            "장학금 및 학비감면 서류제출",
            "교직무시험검정원서 접수",
            "폐강교과목 공지(1차) 및 수강정정"
    })
    void proceed_complex_summary_with_action_keywords(String summary) {
        // given
        Transparent transparent = Transparent.TRANSPARENT;

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("null Transparent와 액션 키워드가 있는 summary")
    @Test
    void proceed_null_transparent_with_action_keyword() {
        // given
        Transparent transparent = null;
        String summary = "수강신청";

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("null Transparent와 액션 키워드가 없는 summary")
    @Test
    void proceed_null_transparent_without_action_keyword() {
        // given
        Transparent transparent = null;
        String summary = "개강";

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("OPAQUE이면 summary 내용과 관계없이 항상 알림")
    @ParameterizedTest
    @ValueSource(strings = {
            "개강",
            "방학",
            "축제",
            "중간고사",
            "기말고사",
            "학술제"
    })
    void proceed_opaque_always_notify_regardless_of_summary(String summary) {
        // given
        Transparent transparent = Transparent.OPAQUE;

        // when
        boolean result = AcademicEventNotificationClassifier.proceed(transparent, summary);

        // then
        assertThat(result).isTrue();
    }
}
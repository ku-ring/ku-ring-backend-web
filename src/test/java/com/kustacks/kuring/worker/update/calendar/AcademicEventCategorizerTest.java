package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.AcademicEventCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AcademicEventCategorizer 단위 테스트")
class AcademicEventCategorizerTest {

    @DisplayName("학적/학위 카테고리 분류")
    @ParameterizedTest
    @ValueSource(strings = {
            "휴학신청", "복학신청", "재입학접수", "졸업유예접수", "조기졸업", "졸업논문입력",
            "전과신청", "다부전공신청", "교직다전공", "교직이수예정자", "교직무시험검정원서"
    })
    void categorize_academic_degree_keywords(String summary) {
        // when
        AcademicEventCategory result = AcademicEventCategorizer.categorize(summary);

        // then
        assertThat(result).isEqualTo(AcademicEventCategory.ACADEMIC_DEGREE);
    }

    @DisplayName("등록/수강/성적 카테고리 분류")
    @ParameterizedTest
    @ValueSource(strings = {
            "수강정정", "수강신청", "등록금납입", "취득학점포기", "성적이의신청",
            "장학금신청", "폐강교과목공지", "환불마감", "강의시간표조회"
    })
    void categorize_registration_course_grade_keywords(String summary) {
        // when
        AcademicEventCategory result = AcademicEventCategorizer.categorize(summary);

        // then
        assertThat(result).isEqualTo(AcademicEventCategory.REGISTRATION_COURSE_GRADE);
    }

    @DisplayName("학사 운영/행사 카테고리 분류")
    @ParameterizedTest
    @ValueSource(strings = {
            "중간고사", "기말고사", "개강", "하계방학", "동계방학", "학위수여식",
            "졸업대상자선정", "예술제", "일감호축전", "개교기념일", "제적대상자알림"
    })
    void categorize_academic_operation_event_keywords(String summary) {
        // when
        AcademicEventCategory result = AcademicEventCategorizer.categorize(summary);

        // then
        assertThat(result).isEqualTo(AcademicEventCategory.ACADEMIC_OPERATION_EVENT);
    }

    @DisplayName("기타 카테고리 분류")
    @Test
    void categorize_etc_for_unknown_keywords() {
        // given
        String summary = "알 수 없는 내용";

        // when
        AcademicEventCategory result = AcademicEventCategorizer.categorize(summary);

        // then
        assertThat(result).isEqualTo(AcademicEventCategory.ETC);
    }

    @DisplayName("null 또는 빈 문자열은 기타로 분류")
    @Test
    void categorize_null_or_empty_as_etc() {
        // given
        String[] testCases = {null, "", "   "};

        for (String summary : testCases) {
            // when
            AcademicEventCategory result = AcademicEventCategorizer.categorize(summary);

            // then
            assertThat(result).isEqualTo(AcademicEventCategory.ETC);
        }
    }
}
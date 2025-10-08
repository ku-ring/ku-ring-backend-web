package com.kustacks.kuring.worker.update.calendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AcademicEventSummaryNormalizer 단위 테스트")
class AcademicEventSummaryNormalizerTest {


    @DisplayName("※ 기호 이후 텍스트 제거")
    @ParameterizedTest
    @CsvSource({
            "'취득학점포기(3차)※주말(토,일)미포함', '취득학점포기(3차)'",
            "'수강신청※자세한 내용은 홈페이지 참조', '수강신청'",
            "'등록금납입※기한 엄수', '등록금납입'",
            "'중간고사※시험 일정표 확인', '중간고사'"
    })
    void normalize_removeTextAfterSymbol(String input, String expected) {
        // when
        String result = AcademicEventSummaryNormalizer.normalize(input);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("괄호 안 날짜/시간 정보 제거 (차수 정보는 유지)")
    @ParameterizedTest
    @CsvSource({
            "전체학년 수강신청 (2. 25. 화 9:30 ~ 2. 26 수 17:00), 전체학년 수강신청",
            "강의시간표조회 (9:30 ~), 강의시간표조회",
            "성적입력 (10:00~18:00), 성적입력",
            "취득학점포기(3차), 취득학점포기(3차)",
            "재수강신청(2차), 재수강신청(2차)",
            "장학금신청(1차), 장학금신청(1차)"
    })
    void normalize_removeDateTimeBrackets_preserveSequence(String input, String expected) {
        // when
        String result = AcademicEventSummaryNormalizer.normalize(input);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("괄호 없는 직접 날짜/시간 정보 제거")
    @ParameterizedTest
    @CsvSource({
            "취득학점포기(3차) 14.(월) 10:30 ~ 15.(화) 16:30, 취득학점포기(3차)",
            "'최정성적이의신청 ~ 5.(월) 16:30', '최정성적이의신청'",
            "수강신청 25.(화) 09:00 ~ 26.(수) 17:00, 수강신청",
            "등록금납입 1.(금) 08:00 ~ 31.(일) 23:59, 등록금납입"
    })
    void normalize_removeDateTime(String input, String expected) {
        // when
        String result = AcademicEventSummaryNormalizer.normalize(input);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("단순 시간 정보 제거 (HH:MM)")
    @ParameterizedTest
    @CsvSource({
            "'복학신청 마감 17:00', '복학신청 마감'",
            "'수강신청 09:30', '수강신청'",
            "'등록금납입 23:59', '등록금납입'",
            "'장학금신청 14:30', '장학금신청'",
            "'성적이의신청 8:00', '성적이의신청'"
    })
    void normalize_removeSimpleTime(String input, String expected) {
        // when
        String result = AcademicEventSummaryNormalizer.normalize(input);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("시간이 아닌 콜론 내용은 보존")
    @ParameterizedTest
    @CsvSource({
            "'전공:컴퓨터공학', '전공:컴퓨터공학'",
            "'학과:전자공학', '학과:전자공학'",
            "'카테고리:A급', '카테고리:A급'"
    })
    void normalize_preserveNonTimeColonContent(String input, String expected) {
        // when
        String result = AcademicEventSummaryNormalizer.normalize(input);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
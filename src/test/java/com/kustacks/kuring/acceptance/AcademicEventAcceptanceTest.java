package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_날짜범위_조회_요청;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_날짜범위_조회_응답_확인;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_시작일만_조회_요청;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_시작일만_조회_응답_확인;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_전체_조회_요청;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_전체_조회_응답_확인;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_조회_오류_응답_검증;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_종료일만_조회_요청;
import static com.kustacks.kuring.acceptance.AcademicEventStep.학사일정_종료일만_조회_응답_확인;

@DisplayName("인수 : 학사일정")
class AcademicEventAcceptanceTest extends IntegrationTestSupport {

    @DisplayName("[v2] 학사일정을 전체 조회한다")
    @Test
    void look_up_all_academic_events() {
        // when
        var 학사일정_조회_응답 = 학사일정_전체_조회_요청();

        // then
        학사일정_전체_조회_응답_확인(학사일정_조회_응답, 5);
    }

    @DisplayName("[v2] 날짜 범위별 학사일정 조회")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "'특정 날짜 범위로 조회', 2025-03-01, 2025-03-31, 3",
            "'3월 전체', 2025-03-01, 2025-03-31, 3",
            "'3월 중순만', 2025-03-02, 2025-03-30, 1",
            "'2월 28일만', 2025-02-28, 2025-02-28, 1",
            "'4월 1일만', 2025-04-01, 2025-04-01, 1"
    })
    void look_up_academic_events_by_date_range(String testName, String startDate, String endDate, int expectedCount) {
        // when
        var 학사일정_조회_응답 = 학사일정_날짜범위_조회_요청(startDate, endDate);

        // then
        학사일정_날짜범위_조회_응답_확인(학사일정_조회_응답, startDate, endDate, expectedCount);
    }

    @DisplayName("[v2] 시작일만으로 학사일정을 조회한다")
    @Test
    void look_up_academic_events_by_start_date_only() {
        // when
        var 학사일정_조회_응답 = 학사일정_시작일만_조회_요청("2025-03-01");

        // then
        학사일정_시작일만_조회_응답_확인(학사일정_조회_응답, "2025-03-01", 4);
    }

    @DisplayName("[v2] 종료일만으로 학사일정을 조회한다")
    @Test
    void look_up_academic_events_by_end_date_only() {
        // when
        var 학사일정_조회_응답 = 학사일정_종료일만_조회_요청("2025-03-31");

        // then
        학사일정_종료일만_조회_응답_확인(학사일정_조회_응답, "2025-03-31", 4);
    }

    @DisplayName("[v2] 잘못된 날짜 형식으로 요청한다")
    @Test
    void invalid_date_format_request() {
        // when
        var 학사일정_조회_응답 = 학사일정_날짜범위_조회_요청("20250301", "20250331");

        // then
        학사일정_조회_오류_응답_검증(학사일정_조회_응답, HttpStatus.BAD_REQUEST, "파라미터 값 중 잘못된 값이 있습니다.");
    }

    @DisplayName("[v2] 시작일이 종료일보다 늦은 경우")
    @Test
    void start_date_after_end_date_request() {
        // when
        var 학사일정_조회_응답 = 학사일정_날짜범위_조회_요청("2025-03-31", "2025-03-01");

        // then
        학사일정_조회_오류_응답_검증(학사일정_조회_응답, HttpStatus.BAD_REQUEST, "시작일, 종료일 설정이 잘못되었습니다.");
    }
}
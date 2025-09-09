package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AcademicEventStep {

    public static ExtractableResponse<Response> 학사일정_전체_조회_요청() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v2/academic-events")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 학사일정_날짜범위_조회_요청(String startDate, String endDate) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .when()
                .get("/api/v2/academic-events")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 학사일정_시작일만_조회_요청(String startDate) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("startDate", startDate)
                .when()
                .get("/api/v2/academic-events")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 학사일정_종료일만_조회_요청(String endDate) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("endDate", endDate)
                .when()
                .get("/api/v2/academic-events")
                .then().log().all()
                .extract();
    }

    public static void 학사일정_조회_정상_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("학사일정 조회에 성공했습니다."),
                () -> assertThat(response.jsonPath().getList("data")).isNotNull()
        );
    }

    public static void 학사일정_전체_조회_응답_확인(ExtractableResponse<Response> response, int expectedCount) {
        학사일정_조회_정상_응답_확인(response);
        var eventData = response.jsonPath().getList("data");
        assertThat(eventData).hasSize(expectedCount);
    }

    public static void 학사일정_날짜범위_조회_응답_확인(ExtractableResponse<Response> response, String startDate, String endDate, int expectedCount) {
        학사일정_조회_정상_응답_확인(response);

        var eventData = response.jsonPath().getList("data");
        assertThat(eventData).hasSize(expectedCount);

        LocalDate queryStartDate = LocalDate.parse(startDate);
        LocalDate queryEndDate = LocalDate.parse(endDate);

        eventData.forEach(event -> 검증_이벤트_날짜범위(event, queryStartDate, queryEndDate));
    }

    public static void 학사일정_시작일만_조회_응답_확인(ExtractableResponse<Response> response, String startDate, int expectedCount) {
        학사일정_조회_정상_응답_확인(response);

        var eventData = response.jsonPath().getList("data");
        assertThat(eventData).hasSize(expectedCount);

        LocalDate queryStartDate = LocalDate.parse(startDate);

        eventData.forEach(event -> {
            Map<String, Object> eventMap = (Map<String, Object>) event;
            LocalDate eventStartDate = parseEventDateTime((String) eventMap.get("startTime"));
            assertThat(eventStartDate).isAfterOrEqualTo(queryStartDate);
        });
    }

    public static void 학사일정_종료일만_조회_응답_확인(ExtractableResponse<Response> response, String endDate, int expectedCount) {
        학사일정_조회_정상_응답_확인(response);

        var eventData = response.jsonPath().getList("data");
        assertThat(eventData).hasSize(expectedCount);

        LocalDate queryEndDate = LocalDate.parse(endDate);

        eventData.forEach(event -> {
            Map<String, Object> eventMap = (Map<String, Object>) event;
            LocalDate eventEndDate = parseEventDateTime((String) eventMap.get("endTime"));
            assertThat(eventEndDate).isBeforeOrEqualTo(queryEndDate);
        });
    }

    private static void 검증_이벤트_날짜범위(Object event, LocalDate queryStartDate, LocalDate queryEndDate) {
        Map<String, Object> eventMap = (Map<String, Object>) event;
        LocalDate eventStartDate = parseEventDateTime((String) eventMap.get("startTime"));
        LocalDate eventEndDate = parseEventDateTime((String) eventMap.get("endTime"));

        assertAll(
                () -> assertThat(eventStartDate).isAfterOrEqualTo(queryStartDate),
                () -> assertThat(eventEndDate).isBeforeOrEqualTo(queryEndDate)
        );
    }

    private static LocalDate parseEventDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr).toLocalDate();
    }

    public static void 학사일정_조회_오류_응답_검증(ExtractableResponse<Response> response, HttpStatus status, String message) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(status.value()),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(status.value()),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo(message),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isFalse()
        );
    }
}
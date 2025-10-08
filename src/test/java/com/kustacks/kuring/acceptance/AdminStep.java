package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.admin.adapter.in.web.dto.AdminAlertCreateRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AdminStep {

    public static void 피드백_조회_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("피드백 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.feedbacks")).hasSize(5)
        );
    }

    public static void 신고_목록_조회_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("신고 목록 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.reports")).hasSize(3)
        );
    }

    public static void 금칙어_로드_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("금칙어 로드에 성공했습니다."),
                () -> assertThat(response.jsonPath().getList("data")).isNull()
        );
    }

    public static void 허용_단어_로드_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("허용 단어 로드에 성공했습니다."),
                () -> assertThat(response.jsonPath().getList("data")).isNull()
        );
    }

    public static ExtractableResponse<Response> 사용자_피드백_조회_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/feedbacks?page=0&size=10")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 신고_목록_조회_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/reports?page=0&size=10")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 알림_예약(String accessToken, AdminAlertCreateRequest alertCreateCommand) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(alertCreateCommand)
                .when().post("/api/v2/admin/alerts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 예약_알림_조회(String accessToken) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/alerts?page=0&size=10")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 예약_알림_삭제(String accessToken, int alertId) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/api/v2/admin/alerts/{id}", alertId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 금칙어_로드_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/v2/admin/badwords/reload")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 허용_단어_로드_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/v2/admin/whitelist-words/reload")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 사용자_토픽_재구독_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/v2/admin/users/subscriptions/all")
                .then().log().all()
                .extract();
    }

    public static void 사용자_토픽_재구독_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("사용자들의 구독 정보를 성공적으로 재설정했습니다."),
                () -> assertThat(response.jsonPath().getString("data")).isNull()
        );
    }
}

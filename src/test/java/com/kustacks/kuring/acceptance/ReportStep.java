package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.report.adapter.in.web.dto.ReportRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ReportStep {

    public static void 신고_요청_응답_확인(ExtractableResponse<Response> response, int statusCode, String message) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(statusCode),
                () -> assertThat(response.body().jsonPath().getInt("code")).isEqualTo(statusCode),
                () -> assertThat(response.body().jsonPath().getString("message")).isEqualTo(message),
                () -> assertThat(response.body().jsonPath().getString("data")).isNull()
        );
    }

    public static void 신고_요청_에러_응답_확인(ExtractableResponse<Response> response, int statusCode, String message) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(statusCode),
                () -> assertThat(response.body().jsonPath().getInt("resultCode")).isEqualTo(statusCode),
                () -> assertThat(response.body().jsonPath().getString("resultMsg")).isEqualTo(message),
                () -> assertThat(response.body().jsonPath().getBoolean("isSuccess")).isFalse()
        );
    }

    public static ExtractableResponse<Response> 신고_요청(String token, Long targetId, String reportType, String content) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", token)
                .body(new ReportRequest(targetId, reportType, content))
                .when().post("/api/v2/reports")
                .then().log().all()
                .extract();
    }
}

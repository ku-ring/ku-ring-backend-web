package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class StaffStep {

    public static void 교직원_조회_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("교직원 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.staffList").size()).isNotZero()
        );
    }

    public static ExtractableResponse<Response> 교직원_조회_요청(String content) {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/staffs/search?content={content}", content)
                .then().log().all()
                .extract();
    }
}

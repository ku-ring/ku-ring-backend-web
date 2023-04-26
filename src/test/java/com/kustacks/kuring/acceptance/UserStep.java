package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.user.common.SubscribeDepartmentsRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UserStep {

    public static void 학과_구독_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("지원하는 학과 구독에 성공하였습니다")
        );
    }

    public static ExtractableResponse<Response> 학과_구독_요청(String token, List<String> departments) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(new SubscribeDepartmentsRequest(token, departments))
                .when().post("/api/v2/users/departments/subscribe")
                .then().log().all()
                .extract();
    }
}

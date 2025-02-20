package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.email.adapter.in.web.dto.EmailVerificationRequest;
import com.kustacks.kuring.email.adapter.in.web.dto.EmailVerifyCodeRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EmailStep {

    public static ExtractableResponse<Response> 인증_이메일_전송_요청(String email) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new EmailVerificationRequest(email))
                .when().post("/api/v2/verification-code")
                .then().log().all()
                .extract();
    }


    public static void 인증_이메일_전송_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("이메일 전송에 성공했습니다."),
                () -> assertThat(response.jsonPath().getList("data")).isNull()
        );
    }

    public static ExtractableResponse<Response> 인증코드_인증_요청(String email, String code) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new EmailVerifyCodeRequest(email, code))
                .when().post("/api/v2/verification-code/verify")
                .then().log().all()
                .extract();
    }


    public static void 인증코드_인증_응답_확인(ExtractableResponse<Response> response, HttpStatus expectedStatus) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(expectedStatus.value())
        );

    }
}
package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.user.adapter.in.web.dto.UserFeedbackRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class FeedbackStep {

    public static void 피드백_요청_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("피드백 저장에 성공하였습니다")
        );
    }

    public static ExtractableResponse<Response> 피드백_요청(String fcmToken, String feedback) {
        return RestAssured
                .given().log().all()
                .header("User-Token", fcmToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new UserFeedbackRequest(feedback))
                .when().post("/api/v2/users/feedbacks")
                .then().log().all()
                .extract();
    }
}

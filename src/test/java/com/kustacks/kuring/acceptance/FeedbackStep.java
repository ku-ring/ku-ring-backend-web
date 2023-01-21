package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.feedback.common.dto.request.SaveFeedbackRequestDto;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class FeedbackStep {

    public static void 피드백_요청_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isEqualTo(true),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo("성공"),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(201)
        );
    }

    public static ExtractableResponse<Response> 피드백_요청(String fcmToken, String feedback) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SaveFeedbackRequestDto(fcmToken, feedback))
                .when().post("/api/v1/feedback")
                .then().log().all()
                .extract();
    }
}

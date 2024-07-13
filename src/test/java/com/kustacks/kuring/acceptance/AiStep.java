package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.ai.adapter.in.web.dto.UserQuestionRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AiStep {

    public static FluxExchangeResult<String> 사용자_질문_요청(WebTestClient client, String question, String userToken) {
        return client
                .post()
                .uri("/api/v2/ai/messages")
                .header("User-Token", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .body(BodyInserters.fromValue(question))
                .exchange()
                .returnResult(String.class);
    }

    // REST 버전이 따로 있는 이유는 예외가 발생하는 경우에는 Json 응답이 오기 때문이다
    public static ExtractableResponse<Response> 사용자_질문_요청_REST(String question, String userToken) {
        return RestAssured
                .given().log().all()
                .header("User-Token", userToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new UserQuestionRequest(question))
                .when().post("/api/v2/ai/messages")
                .then().log().all()
                .extract();
    }

    public static void 모델_응답_검증(FluxExchangeResult<String> response, int statusCode) {
        String bodyAsString = response.getResponseBody()
                .reduce(new StringBuilder(), StringBuilder::append)
                .map(StringBuilder::toString)
                .block();

        assertAll(
                () -> assertThat(response.getStatus().value()).isEqualTo(statusCode),
                () -> assertThat(bodyAsString).contains("02-450-3211~2이며", "02-450-3967")
        );
    }
}

package com.kustacks.kuring.acceptance;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.OK;

public class AiStep {

    public static FluxExchangeResult<String> 사용자_질문_요청(WebTestClient client, String question, String userToken) {
        return client
                .get()
                .uri("/api/v2/ai/messages?question={question}", question)
                .header("User-Token", userToken)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .returnResult(String.class);
    }

    public static FluxExchangeResult<String> 루트유저_사용자_질문_요청(WebTestClient client, String question, String userToken, String accessToken) {
        return client
                .get()
                .uri("/api/v2/ai/messages?question={question}", question)
                .header("User-Token", userToken)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .returnResult(String.class);
    }

    public static void 모델_응답_확인(FluxExchangeResult<String> 모델_응답, String... expected) {
        assertAll(
                () -> assertThat(모델_응답.getStatus()).isEqualTo(OK),
                () -> StepVerifier.create(모델_응답.getResponseBody())
                        .expectNext(expected)
                        .expectComplete()
                        .verify()
        );
    }
}

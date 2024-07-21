package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.kustacks.kuring.acceptance.AiStep.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("인수 : 인공지능")
class AiAcceptanceTest extends IntegrationTestSupport {

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 교내,외 장학금 및 학자금 대출 관련 전화번호들을 묻는다
     * Then : 해당 관련 전화번호들을 반환한다
     */
    @DisplayName("[v2] 사용자가 궁금한 학교 정보를 물어볼 수 있다")
    @Test
    void ask_to_open_ai() {
        // given
        WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        String question = "교내,외 장학금 및 학자금 대출 관련 전화번호들을 안내를 해줘";

        // when
        var 모델_응답 = 사용자_질문_요청(client, question, USER_FCM_TOKEN);

        // then
        모델_응답_검증(모델_응답, HttpStatus.OK.value());
    }

    /**
     * Given : 가능한 질문 횟수를 모두 사용하였다
     * When : AI에게 질문을 한다
     * Then : 질문 토큰이 부족하다는 메시지를 반환한다
     */
    @DisplayName("[v2] 가능한 질문 횟수를 모두 사용한 경우 AI에게 질문을 할 수 없다")
    @Test
    void ask_to_open_ai_overflow_count() {
        // given
        String question = "교내,외 장학금 및 학자금 대출 관련 전화번호들을 안내를 해줘";
        사용자_질문_요청_REST(question, USER_FCM_TOKEN);
        사용자_질문_요청_REST(question, USER_FCM_TOKEN);
        사용자_질문_요청_REST(question, USER_FCM_TOKEN);

        // when
        var 모델_응답 = 사용자_질문_요청_REST(question, USER_FCM_TOKEN);

        // then
        assertThat(모델_응답.statusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
    }
}


package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.ai.adapter.in.web.dto.UserQuestionRequest;
import com.kustacks.kuring.support.IntegrationTestSupport;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("인수 : 인공지능")
public class AiAcceptanceTest extends IntegrationTestSupport {

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 사용자가 수업연한초과자 등록기간을 묻는다
     * Then : 수업연한초과자 등록일을 알려준다
     */
    @DisplayName("[v2] 2024년도 2학기 등록일")
    @Test
    public void ask_to_open_ai() {
        // given
        String question = "수업연한초과자 등록기간을 알려줘";

        // when
        var response = RestAssured
                .given().log().all()
                .header("User-Token", USER_FCM_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new UserQuestionRequest(question))
                .when().post("/api/v2/ai/messages")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).contains("2024년 9월 2일(월)부터 9월 6일(금)")
        );
    }
}

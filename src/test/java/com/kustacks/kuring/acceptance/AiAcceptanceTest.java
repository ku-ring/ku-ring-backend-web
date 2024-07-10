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
     * When : 교내,외 장학금 및 학자금 대출 관련 전화번호들을 묻는다
     * Then : 해당 관련 전화번호들을 반환한다
     */
    @DisplayName("[v2] 사용자가 궁금한 학교 정보를 물어볼 수 있다")
    @Test
    public void ask_to_open_ai() {
        // given
        String question = "교내,외 장학금 및 학자금 대출 관련 전화번호들을 안내를 해줘";

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
                () -> assertThat(response.asString()).contains("학생복지처 장학복지팀의 전화번호는 02-450-3211~2이며, " +
                        "건국사랑/장학사정관장학/기금장학과 관련된 문의는 02-450-3967로 하시면 됩니다.")
        );
    }
}


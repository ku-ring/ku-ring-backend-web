package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.ai.adapter.in.web.dto.UserQuestionRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AiStep {

    public static ExtractableResponse<Response> 사용자_질문_요청(String question, String userToken) {
        return RestAssured
                .given().log().all()
                .header("User-Token", userToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new UserQuestionRequest(question))
                .when().post("/api/v2/ai/messages")
                .then().log().all()
                .extract();
    }

    public static void 모델_응답_검증(ExtractableResponse<Response> response, int statusCode) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(statusCode),
                () -> assertThat(response.asString()).contains("학생복지처 장학복지팀의 전화번호는 02-450-3211~2이며, " +
                        "건국사랑/장학사정관장학/기금장학과 관련된 문의는 02-450-3967로 하시면 됩니다.")
        );
    }
}

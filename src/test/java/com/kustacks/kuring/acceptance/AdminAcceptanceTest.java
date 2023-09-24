package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.kustacks.kuring.acceptance.AuthStep.로그인_되어_있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("인수 : 관리자")
class AdminAcceptanceTest extends AcceptanceTest {

    /**
     * Given : 등록된 Admin이 있다.
     * When : hello에 접근시
     * Then : 성공적으로 hello를 반환받는다.
     */
    @Test
    void hello_controller_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/hello")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).isEqualTo("hello")
        );
    }
}

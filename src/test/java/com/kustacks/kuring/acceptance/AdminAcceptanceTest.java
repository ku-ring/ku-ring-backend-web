package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.admin.adapter.in.web.dto.RealNotificationRequest;
import com.kustacks.kuring.admin.adapter.in.web.dto.TestNotificationRequest;
import com.kustacks.kuring.support.IntegrationTestSupport;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.kustacks.kuring.acceptance.AdminStep.사용자_피드백_조회_요청;
import static com.kustacks.kuring.acceptance.AdminStep.피드백_조회_확인;
import static com.kustacks.kuring.acceptance.AuthStep.로그인_되어_있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("인수 : 관리자")
class AdminAcceptanceTest extends IntegrationTestSupport {

    /**
     * given : 사전에 등록된 어드민가 피드백들이 이다
     * when : 어드민이 피드백 조회시
     * then : 성공적으로 조회된다
     */
    @DisplayName("[v2] 사용자 피드백 조회")
    @Test
    void role_root_admin_search_feedbacks() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var 사용자_피드백 = 사용자_피드백_조회_요청(accessToken);

        // then
        피드백_조회_확인(사용자_피드백);
    }

    /**
     * given : 사전에 등록된 어드민이 있다
     * when : 테스트 공지를 발송하면
     * then : 성공적으로 발송된다.
     */
    @DisplayName("[v2] 테스트 공지 발송")
    @Test
    void role_root_admin_create_test_notification() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(new TestNotificationRequest("bachelor", "테스트 공지입니다", "1234"))
                .when().post("/api/v2/admin/notices/dev")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("테스트 공지 생성에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).isNull()
        );
    }

    /**
     * given : 사전에 등록된 어드민이 있다
     * when : 실제 공지를 발송하면
     * then : 성공적으로 발송된다.
     */
    @DisplayName("[v2] 실제 공지 발송")
    @Test
    void role_root_admin_create_real_notification() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        var response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(new RealNotificationRequest("real 공지", "real 공지입니다", "https://www.naver.com", ADMIN_PASSWORD))
                .when().post("/api/v2/admin/notices/prod")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("실제 공지 생성에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).isNull()
        );
    }

    /**
     * Given : 등록된 ROLE_ROOT의 Admin이 있다.
     * When : ROLE_ROOT의 API에 접근시
     * Then : 응답받을 수 있다
     */
    @DisplayName("[v2] Root -> Root API 접근 테스트")
    @Test
    void role_root_admin_call_root_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/root")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data")).contains("ROLE_CLIENT", "ROLE_ROOT")
        );
    }

    /**
     * Given : 등록된 ROLE_ROOT의 Admin이 있다.
     * When : ROLE_CLIENT의 API에 접근시
     * Then : 응답받을 수 있다
     */
    @DisplayName("[v2] Root -> Client API 접근 테스트")
    @Test
    void role_root_admin_call_client_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/client")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).contains("ROLE_CLIENT", "ROLE_ROOT")
        );
    }

    /**
     * Given : 등록된 ROLE_CLIENT의 Admin이 있다.
     * When : ROLE_CLIENT의 API에 접근시
     * Then : 응답받을 수 있다
     */
    @DisplayName("[v2] Client -> Client API 접근 테스트")
    @Test
    void role_client_admin_call_client_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_CLIENT_LOGIN_ID, ADMIN_CLIENT_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/client")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).contains("ROLE_CLIENT")
        );
    }

    /**
     * Given : 등록된 ROLE_CLIENT의 Admin이 있다.
     * When : ROLE_ROOT의 API에 접근시
     * Then : 응답받을 수 없다
     */
    @DisplayName("[v2] Client -> Root API 접근 테스트")
    @Test
    void role_client_admin_call_root_api_test() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_CLIENT_LOGIN_ID, ADMIN_CLIENT_PASSWORD);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/admin/root")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(401),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("인증에 실패하였습니다"),
                () -> assertThat(response.jsonPath().getString("data")).isNull()
        );
    }
}

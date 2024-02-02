package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.support.IntegrationTestSupport;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.AuthStep.로그인_되어_있음;
import static com.kustacks.kuring.acceptance.AuthStep.로그인_요청;
import static com.kustacks.kuring.acceptance.CategoryStep.사용자가_구독한_카테고리_목록_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@DisplayName("인수 : 인증")
class AuthAcceptanceTest extends IntegrationTestSupport {

    @DisplayName("[v2] Bearer Auth login")
    @Test
    void bearer_auth_login_success() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when, then
        assertThat(accessToken).isNotBlank();
    }


    @DisplayName("[v2] 존재하지 않는 id, password 로그인을 시도한다")
    @Test
    void bearer_auth_login_fail() {
        // given
        var response = 로그인_요청("not-exists-id", ADMIN_PASSWORD);

        // when, then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("[v2] 사용자가 잘못된 FCM token으로 요청을 보내면 예외가 발생한다")
    @Test
    void invalid_fcm_token_login_fail() {
        // given
        doThrow(new FirebaseInvalidTokenException())
                .when(firebaseSubscribeService)
                .validationToken(anyString());

        // when
        ExtractableResponse<Response> response = 사용자가_구독한_카테고리_목록_조회_요청(USER_FCM_TOKEN);

        // when, then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}

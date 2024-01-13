package com.kustacks.kuring.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.AuthStep.로그인_되어_있음;
import static com.kustacks.kuring.acceptance.AuthStep.로그인_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("인수 : 인증")
class AuthAcceptanceTest extends AcceptanceTest {

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
}

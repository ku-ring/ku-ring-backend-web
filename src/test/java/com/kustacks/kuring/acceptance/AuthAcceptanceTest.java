package com.kustacks.kuring.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.AuthStep.로그인_되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("인수 : 인증")
public class AuthAcceptanceTest extends AcceptanceTest {

    @DisplayName("Bearer Auth login")
    @Test
    void myInfoWithBearerAuth() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_LOGIN_ID, ADMIN_PASSWORD);

        // when, then
        assertThat(accessToken).isNotBlank();
    }
}

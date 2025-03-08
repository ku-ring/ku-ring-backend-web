package com.kustacks.kuring.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("도메인 : RootUser")
class RootUserTest {

    @DisplayName("RootUser 생성 테스트")
    @Test
    void creat_user() {
        //given
        String email = "client@konkuk.ac.kr";
        String password = "123456";
        String nickname = "쿠링이";

        //when, then
        assertThatCode(() -> new RootUser(email, password, nickname))
                .doesNotThrowAnyException();
    }
}
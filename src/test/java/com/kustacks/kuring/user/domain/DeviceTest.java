package com.kustacks.kuring.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("도메인 : Device")
class DeviceTest {


    @DisplayName("Device 생성 테스트")
    @Test
    void creat_user() {
        //given
        String token = "token";
        User user = createUser(1L, token);

        //when, then
        assertThatCode(() -> new Device("token" ,user))
                .doesNotThrowAnyException();
    }

    @DisplayName("디바이스에 로그인한다.")
    @Test
    void login_user() {
        // given
        User tokenUser = createUser(1L, "token");
        User emailUser = createEmailUser(2L, "client@konkuk.ac.kr","1234","nickname");
        Device device = new Device("token", tokenUser);

        // when
        device.login(emailUser);

        // then
        assertThat(device.getUser())
                .isEqualTo(emailUser);
    }

    @DisplayName("디바이스에서 로그아웃한다.")
    @Test
    void logout_user() {
        // given
        User tokenUser = createUser(1L, "token");
        User emailUser = createEmailUser(2L, "client@konkuk.ac.kr","1234","nickname");
        Device device = new Device("token", tokenUser);
        emailUser.login(device);

        // when
        device.logout();

        // then
        assertThat(device.getUser())
                .isEqualTo(tokenUser);
    }


    private User createUser(Long id, String token) {
        User user = new User(token);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private User createEmailUser(Long id, String email, String password, String nickname) {
        User emailUser = new User(email, password, nickname);
        ReflectionTestUtils.setField(emailUser,"id",id);
        return emailUser;
    }

}
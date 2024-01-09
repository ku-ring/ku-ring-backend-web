package com.kustacks.kuring.admin.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("도메인 : Admin")
class AdminTest {

    @DisplayName("Admin 생성 테스트")
    @Test
    void creat_user() {
        assertThatCode(() -> new Admin("token", "password"))
                .doesNotThrowAnyException();
    }

    @DisplayName("Admin의 권한을 확인한다")
    @Test
    void add_category() {
        // given
        Admin admin = new Admin("token", "password");

        // when
        admin.addRole(AdminRole.ROLE_CLIENT);
        admin.addRole(AdminRole.ROLE_ROOT);

        // then
        assertThat(admin.getAuthorities()).contains("ROLE_CLIENT", "ROLE_ROOT");
    }
}

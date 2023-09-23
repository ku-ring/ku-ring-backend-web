package com.kustacks.kuring.auth.userdetails;

import com.kustacks.kuring.admin.domain.AdminRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AdminUserDetailsTest {

    private static final String ADMIN_ID = "adminId";
    private static final String ADMIN_PASSWORD = "password";
    private static final List<String> ADMIN_ROLES = List.of(AdminRole.ROLE_ROOT.name(), AdminRole.ROLE_CLIENT.name());

    @Test
    void test() {
        // given
        AdminUserDetails userDetails = new AdminUserDetails(ADMIN_ID, ADMIN_PASSWORD,
                List.of(AdminRole.ROLE_ROOT.name(), AdminRole.ROLE_CLIENT.name()));

        // when, then
        assertAll(
                () -> assertThat(userDetails.getPrincipal()).isEqualTo(ADMIN_ID),
                () -> assertThat(userDetails.getPassword()).isEqualTo(ADMIN_PASSWORD),
                () -> assertThat(userDetails.getAuthorities()).isEqualTo(ADMIN_ROLES)
        );
    }
}

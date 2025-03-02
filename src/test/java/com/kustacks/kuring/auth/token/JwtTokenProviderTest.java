package com.kustacks.kuring.auth.token;

import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.auth.exception.UnauthorizedException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class JwtTokenProviderTest {

    private String secretKey = "test-secret-key-test-secret-key-test-secret-key-test-secret-key";
    private long expireLength = 3600000;
    private long userExpireLength = 3600000;

    @Test
    @DisplayName("토큰을 성공적으로 발급하고 payload 추출한다")
    void parse_payload_by_valid_token() {
        // given
        JwtTokenProperties properties = new JwtTokenProperties(secretKey, expireLength, userExpireLength);
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(properties);
        String userId = "shine_id";

        // when
        String token = jwtTokenProvider.createAdminToken(userId, List.of(AdminRole.ROLE_ROOT.name(), AdminRole.ROLE_CLIENT.name()));

        // then
        assertAll(
                () -> assertThat(jwtTokenProvider.getPrincipal(token)).isEqualTo(userId),
                () -> assertThat(jwtTokenProvider.getRoles(token)).isEqualTo(List.of(AdminRole.ROLE_ROOT.name(), AdminRole.ROLE_CLIENT.name()))
        );
    }

    @Test
    @DisplayName("만료된 토큰에서 payload 추출 시 예외를 반환한다")
    void parse_payload_by_expired_token() {
        // given
        JwtTokenProperties properties = new JwtTokenProperties(secretKey, -1l, -1l);
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(properties);
        String userId = "shine_id";
        String expiredToken = jwtTokenProvider.createAdminToken(userId, List.of(AdminRole.ROLE_ROOT.name(), AdminRole.ROLE_CLIENT.name()));

        // when
        ThrowingCallable callable = () -> jwtTokenProvider.getPrincipal(expiredToken);

        // then
        assertThatThrownBy(callable).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("잘못된 키로 토큰을 발급할 경우 payload 추출 시 예외를 반환한다")
    void parse_payload_by_invalid_token() {
        // given
        JwtTokenProperties validProperties = new JwtTokenProperties(secretKey, expireLength, userExpireLength);
        JwtTokenProperties invalidProperties = new JwtTokenProperties("invalid-key", expireLength, userExpireLength);

        JwtTokenProvider validJwtTokenProvider = new JwtTokenProvider(validProperties);
        JwtTokenProvider invalidJwtTokenProvider = new JwtTokenProvider(invalidProperties);

        String userId = "shine_id";
        String token = validJwtTokenProvider.createAdminToken(userId, List.of(AdminRole.ROLE_ROOT.name(), AdminRole.ROLE_CLIENT.name()));

        // when
        ThrowingCallable callable = () -> invalidJwtTokenProvider.getPrincipal(token);

        // then
        assertAll(
                () -> assertThatThrownBy(callable).isInstanceOf(UnauthorizedException.class),
                () -> assertThat(invalidJwtTokenProvider.validateToken(token)).isFalse()
        );
    }
}

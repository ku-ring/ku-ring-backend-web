package com.kustacks.kuring.auth.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationExtractorTest {

    @Test
    @DisplayName("사용자의 토큰을 추출한다")
    public void extract_user_id_and_roles() {
        // given
        String userToken = "test-token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + userToken);

        // when
        String actual = AuthorizationExtractor.extract(request, AuthorizationType.BEARER);

        // then
        assertThat(actual).isEqualTo(userToken);
    }

    @Test
    @DisplayName("토큰 형식이 Bearer로 시작하지 않으면 빈 문자열을 반환한다")
    public void extract_not_start_bearer() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "token");

        // when
        String actual = AuthorizationExtractor.extract(request, AuthorizationType.BEARER);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("토큰에 헤더는 있지만 토큰이 존재하지 않으면 빈 문자열을 반환한다")
    public void extract_not_exist_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer ");

        // when
        String actual = AuthorizationExtractor.extract(request, AuthorizationType.BEARER);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("토큰이 존재하지 않으면 빈 문자열을 반환한다")
    public void extract_not_exist_header() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String actual = AuthorizationExtractor.extract(request, AuthorizationType.BEARER);

        // then
        assertThat(actual).isEmpty();
    }
}

package com.kustacks.kuring.auth.interceptor;

import com.kustacks.kuring.auth.authentication.AuthenticationException;
import com.kustacks.kuring.auth.authentication.AuthenticationToken;
import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.exception.UnauthorizedException;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.auth.userdetails.AdminUserDetails;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class BearerTokenAuthenticationFilter extends AuthenticationChainingFilter {

    private static final String BEARER_DELIMITER = "Bearer ";
    private JwtTokenProvider jwtTokenProvider;

    public BearerTokenAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthenticationToken convert(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_DELIMITER)) {
            throw new AuthenticationException();
        }

        String token = AuthorizationExtractor.extract(request, AuthorizationType.BEARER);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException();
        }

        String principal = jwtTokenProvider.getPrincipal(token);
        return new AuthenticationToken(principal, token);
    }

    @Override
    public AdminUserDetails findUserDetails(AuthenticationToken token) {
        String principal = token.getPrincipal(); // loginId
        String credentials = token.getCredentials(); // jwtToken
        List<String> roles = jwtTokenProvider.getRoles(credentials);
        return AdminUserDetails.of(principal, roles);
    }
}

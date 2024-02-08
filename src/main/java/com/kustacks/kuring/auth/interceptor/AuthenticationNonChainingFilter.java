package com.kustacks.kuring.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.authentication.AuthenticationException;
import com.kustacks.kuring.auth.authentication.AuthenticationToken;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.handler.AuthenticationFailureHandler;
import com.kustacks.kuring.auth.handler.AuthenticationSuccessHandler;
import com.kustacks.kuring.auth.userdetails.UserDetails;
import com.kustacks.kuring.auth.userdetails.UserDetailsServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public abstract class AuthenticationNonChainingFilter implements HandlerInterceptor {

    protected final UserDetailsServicePort userDetailsServicePort;
    protected final PasswordEncoder passwordEncoder;
    protected final ObjectMapper objectMapper;
    protected final AuthenticationSuccessHandler successHandler;
    protected final AuthenticationFailureHandler failureHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            AuthenticationToken tokenRequest = convert(request);
            Authentication authentication = authenticate(tokenRequest);
            afterAuthentication(request, response, authentication);
            return false;
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response, e);
            return false;
        }
    }

    public Authentication authenticate(AuthenticationToken tokenRequest) {
        String principal = tokenRequest.getPrincipal();
        String credentials = tokenRequest.getCredentials();

        UserDetails userDetails = userDetailsServicePort.loadUserByUsername(principal);
        if (userDetails == null) {
            throw new AuthenticationException();
        }

        if (isNotMatchPassword(credentials, userDetails)) {
            throw new AuthenticationException();
        }

        return new Authentication(userDetails.getPrincipal(), userDetails.getAuthorities());
    }

    protected void afterAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Exception failed) throws IOException {
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    protected abstract AuthenticationToken convert(HttpServletRequest request) throws IOException;

    private boolean isNotMatchPassword(String password, UserDetails userDetails) {
        return !passwordEncoder.matches(password, userDetails.getPassword());
    }
}

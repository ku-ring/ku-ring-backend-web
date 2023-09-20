package com.kustacks.kuring.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.authentication.AuthenticationToken;
import com.kustacks.kuring.auth.handler.AuthenticationFailureHandler;
import com.kustacks.kuring.auth.handler.AuthenticationSuccessHandler;
import com.kustacks.kuring.auth.token.AdminLoginTokenRequest;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.auth.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public class AdminTokenAuthenticationFilter extends AuthenticationNonChainingFilter {

    public AdminTokenAuthenticationFilter(UserDetailsService userDetailsService,
                                          JwtTokenProvider jwtTokenProvider,
                                          ObjectMapper objectMapper,
                                          AuthenticationSuccessHandler successHandler,
                                          AuthenticationFailureHandler failureHandler)
    {
        super(userDetailsService, jwtTokenProvider, objectMapper, successHandler, failureHandler);
    }

    @Override
    public AuthenticationToken convert(HttpServletRequest request) throws IOException {
        String content = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));

        AdminLoginTokenRequest adminLoginTokenRequest = objectMapper.readValue(content, AdminLoginTokenRequest.class);
        return new AuthenticationToken(adminLoginTokenRequest.getLoginId(), adminLoginTokenRequest.getPassword());
    }
}

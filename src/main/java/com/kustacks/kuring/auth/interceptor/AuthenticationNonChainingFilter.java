package com.kustacks.kuring.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.authentication.AuthenticationException;
import com.kustacks.kuring.auth.authentication.AuthenticationToken;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.auth.userdetails.UserDetails;
import com.kustacks.kuring.auth.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AuthenticationNonChainingFilter implements HandlerInterceptor {

    protected final UserDetailsService userDetailsService;
    protected final JwtTokenProvider jwtTokenProvider;
    protected final ObjectMapper objectMapper;

    public AuthenticationNonChainingFilter(UserDetailsService userDetailsService,
                                           JwtTokenProvider jwtTokenProvider,
                                           ObjectMapper objectMapper
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AuthenticationToken tokenRequest = convert(request);
        Authentication authentication = authenticate(tokenRequest);
        afterAuthentication(authentication, response);
        return false;
    }

    public Authentication authenticate(AuthenticationToken tokenRequest) {
        String principal = tokenRequest.getPrincipal();
        String credentials = tokenRequest.getCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(principal);
        if (userDetails == null) {
            throw new AuthenticationException();
        }

        if (!userDetails.isValidCredentials(credentials)) {
            throw new AuthenticationException();
        }

        return new Authentication(userDetails.getPrincipal(), userDetails.getAuthorities());
    }

    protected abstract AuthenticationToken convert(HttpServletRequest request) throws IOException;

    protected abstract void afterAuthentication(Authentication authentication, HttpServletResponse response);
}

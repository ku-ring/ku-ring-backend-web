package com.kustacks.kuring.auth.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.authentication.AuthenticationToken;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.exception.BuildResponseTokenFailException;
import com.kustacks.kuring.auth.exception.OutputStreamException;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.auth.token.AdminLoginTokenRequest;
import com.kustacks.kuring.auth.token.AdminLoginTokenResponse;
import com.kustacks.kuring.auth.userdetails.UserDetailsService;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class AdminTokenAuthenticationFilter extends AuthenticationNonChainingFilter {

    public AdminTokenAuthenticationFilter(UserDetailsService userDetailsService,
                                          JwtTokenProvider jwtTokenProvider,
                                          ObjectMapper objectMapper)
    {
        super(userDetailsService, jwtTokenProvider, objectMapper);
    }

    @Override
    public AuthenticationToken convert(HttpServletRequest request) throws IOException {
        String content = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));

        AdminLoginTokenRequest adminLoginTokenRequest = objectMapper.readValue(content, AdminLoginTokenRequest.class);
        return new AuthenticationToken(adminLoginTokenRequest.getLoginId(), adminLoginTokenRequest.getPassword());
    }

    @Override
    protected void afterAuthentication(Authentication authentication, HttpServletResponse response) {
        String token = jwtTokenProvider.createToken(String.valueOf(authentication.getPrincipal()), authentication.getAuthorities());
        AdminLoginTokenResponse adminLoginTokenResponse = new AdminLoginTokenResponse(token);

        try {
            String responseToClient = objectMapper.writeValueAsString(adminLoginTokenResponse);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().print(responseToClient);
        } catch (JsonProcessingException e) {
            throw new BuildResponseTokenFailException();
        } catch (IOException e) {
            throw new OutputStreamException();
        }
    }
}

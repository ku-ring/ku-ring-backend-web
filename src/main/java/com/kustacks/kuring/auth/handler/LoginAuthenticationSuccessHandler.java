package com.kustacks.kuring.auth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.exception.BuildResponseTokenFailException;
import com.kustacks.kuring.auth.exception.OutputStreamException;
import com.kustacks.kuring.auth.token.AdminLoginTokenResponse;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.AUTH_AUTHENTICATION_SUCCESS;

@RequiredArgsConstructor
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String token = jwtTokenProvider.createToken(String.valueOf(authentication.getPrincipal()), authentication.getAuthorities());
        AdminLoginTokenResponse adminLoginTokenResponse = new AdminLoginTokenResponse(token);
        BaseResponse<AdminLoginTokenResponse> loginSuccessResponse = new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, adminLoginTokenResponse);

        try {
            String responseToClient = objectMapper.writeValueAsString(loginSuccessResponse);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(responseToClient.getBytes());
        } catch (JsonProcessingException e) {
            throw new BuildResponseTokenFailException();
        } catch (IOException e) {
            throw new OutputStreamException();
        }
    }
}

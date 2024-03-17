package com.kustacks.kuring.auth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.exception.BuildResponseTokenFailException;
import com.kustacks.kuring.auth.exception.OutputStreamException;
import com.kustacks.kuring.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.USER_REGISTER_SUCCESS;

@RequiredArgsConstructor
public class UserRegisterSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            String responseToClient = objectMapper.writeValueAsString(new BaseResponse<>(USER_REGISTER_SUCCESS, null));
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

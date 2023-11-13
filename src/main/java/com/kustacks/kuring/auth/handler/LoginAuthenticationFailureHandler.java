package com.kustacks.kuring.auth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.exception.BuildResponseTokenFailException;
import com.kustacks.kuring.auth.exception.OutputStreamException;
import com.kustacks.kuring.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.AUTH_AUTHENTICATION_FAIL;

@RequiredArgsConstructor
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, Exception failed) throws IOException {
        try {
            String responseToClient = objectMapper.writeValueAsString(new BaseResponse<>(AUTH_AUTHENTICATION_FAIL, null));
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(responseToClient.getBytes());
        } catch (JsonProcessingException e) {
            throw new BuildResponseTokenFailException();
        } catch (IOException e) {
            throw new OutputStreamException();
        }
    }
}

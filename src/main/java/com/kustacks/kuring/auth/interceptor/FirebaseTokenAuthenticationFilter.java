package com.kustacks.kuring.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.authentication.AuthenticationException;
import com.kustacks.kuring.auth.exception.UnauthorizedException;
import com.kustacks.kuring.common.dto.ErrorResponse;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class FirebaseTokenAuthenticationFilter implements HandlerInterceptor {

    private static final String FIREBASE_HEADER = "User-Token";
    private final FirebaseWithUserUseCase firebaseService;
    private final ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        try {
            String authorization = request.getHeader(FIREBASE_HEADER);

            if (authorization == null) {
                throw new AuthenticationException();
            }

            if (authorization.isBlank()) {
                throw new UnauthorizedException();
            }

            firebaseService.validationToken(authorization);

            return true;
        } catch (UnauthorizedException | FirebaseInvalidTokenException e) {
            setErrorResponse(response);
            return false;
        } catch (AuthenticationException e) {
            return true;
        }
    }

    private void setErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String result = objectMapper.writeValueAsString(new ErrorResponse(ErrorCode.API_FB_INVALID_TOKEN));
        response.getWriter().write(result);
    }
}

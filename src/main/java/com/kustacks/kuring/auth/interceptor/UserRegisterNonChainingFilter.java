package com.kustacks.kuring.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.dto.UserRegisterRequest;
import com.kustacks.kuring.auth.exception.RegisterException;
import com.kustacks.kuring.auth.handler.AuthenticationFailureHandler;
import com.kustacks.kuring.auth.handler.AuthenticationSuccessHandler;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.stream.Collectors;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@RequiredArgsConstructor
public class UserRegisterNonChainingFilter implements HandlerInterceptor {

    private static final String REGISTER_HTTP_METHOD = "POST";

    private final ServerProperties serverProperties;
    private final FirebaseWithUserUseCase firebaseService;
    private final UserCommandPort userCommandPort;
    private final ObjectMapper objectMapper;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (request.getMethod().equals(REGISTER_HTTP_METHOD)) {
                String userFcmToken = convert(request);
                register(userFcmToken);
                afterAuthentication(request, response);
                return false;
            }

            throw new RegisterException();
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response, e);
            return false;
        }
    }

    private void register(String userFcmToken) {
        try {
            userCommandPort.save(new User(userFcmToken));
        } catch (DuplicateKeyException e) { // 이미 등록된 사용자에 대한 처리는 필요없다
            log.warn("User already exists: {}", userFcmToken, e);
        }

        UserSubscribeCommand command =
                new UserSubscribeCommand(
                        userFcmToken,
                        serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC)
                );
        firebaseService.subscribe(command);
    }

    public String convert(HttpServletRequest request) throws IOException {
        String content = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));

        UserRegisterRequest userRegisterRequest = objectMapper.readValue(content, UserRegisterRequest.class);
        return userRegisterRequest.getToken();
    }

    protected void afterAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        successHandler.onAuthenticationSuccess(request, response, null);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Exception failed) throws IOException {
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}

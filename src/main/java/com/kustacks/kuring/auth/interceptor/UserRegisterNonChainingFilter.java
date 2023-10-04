package com.kustacks.kuring.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.auth.dto.UserRegisterRequest;
import com.kustacks.kuring.auth.exception.RegisterException;
import com.kustacks.kuring.auth.handler.AuthenticationFailureHandler;
import com.kustacks.kuring.auth.handler.AuthenticationSuccessHandler;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.message.firebase.ServerProperties;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

import static com.kustacks.kuring.message.firebase.FirebaseService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@RequiredArgsConstructor
public class UserRegisterNonChainingFilter implements HandlerInterceptor {

    private static final String REGISTER_HTTP_METHOD = "POST";

    private final ServerProperties serverProperties;
    private final FirebaseService firebaseService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if(request.getMethod().equals(REGISTER_HTTP_METHOD)) {
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
        userRepository.save(new User(userFcmToken));
        firebaseService.subscribe(userFcmToken, serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC));
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

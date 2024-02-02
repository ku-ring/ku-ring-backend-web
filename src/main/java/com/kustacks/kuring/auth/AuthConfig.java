package com.kustacks.kuring.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.admin.application.service.AdminDetailsService;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipalArgumentResolver;
import com.kustacks.kuring.auth.context.SecurityContextPersistenceFilter;
import com.kustacks.kuring.auth.handler.*;
import com.kustacks.kuring.auth.interceptor.AdminTokenAuthenticationFilter;
import com.kustacks.kuring.auth.interceptor.BearerTokenAuthenticationFilter;
import com.kustacks.kuring.auth.interceptor.FirebaseTokenAuthenticationFilter;
import com.kustacks.kuring.auth.interceptor.UserRegisterNonChainingFilter;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.service.ServerProperties;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final AdminDetailsService adminDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final ServerProperties serverProperties;
    private final FirebaseWithUserUseCase firebaseService;
    private final UserPersistenceAdapter userPersistenceAdapter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityContextPersistenceFilter());

        registry.addInterceptor(new AdminTokenAuthenticationFilter(
                adminDetailsService, passwordEncoder(), objectMapper,
                adminLoginSuccessHandler(), adminLoginFailureHandler()))
                .addPathPatterns("/api/v2/admin/login");

        registry.addInterceptor(new UserRegisterNonChainingFilter(
                        serverProperties, firebaseService, userPersistenceAdapter, objectMapper,
                        userRegisterSuccessHandler(), userRegisterFailureHandler()))
                .addPathPatterns("/api/v2/users");

        registry.addInterceptor(new BearerTokenAuthenticationFilter(jwtTokenProvider))
                .addPathPatterns("/api/v2/admin/**");

        registry.addInterceptor(new FirebaseTokenAuthenticationFilter(firebaseService, objectMapper))
                .addPathPatterns("/api/v2/users/**");
    }

    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationSuccessHandler adminLoginSuccessHandler() {
        return new LoginAuthenticationSuccessHandler(jwtTokenProvider, objectMapper);
    }

    @Bean
    AuthenticationFailureHandler adminLoginFailureHandler() {
        return new LoginAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    AuthenticationSuccessHandler userRegisterSuccessHandler() {
        return new UserRegisterSuccessHandler(objectMapper);
    }

    @Bean
    AuthenticationFailureHandler userRegisterFailureHandler() {
        return new UserRegisterFailureHandler(objectMapper);
    }
}

package com.kustacks.kuring.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.admin.business.AdminDetailsService;
import com.kustacks.kuring.auth.context.SecurityContextPersistenceFilter;
import com.kustacks.kuring.auth.handler.AuthenticationFailureHandler;
import com.kustacks.kuring.auth.handler.AuthenticationSuccessHandler;
import com.kustacks.kuring.auth.handler.LoginAuthenticationFailureHandler;
import com.kustacks.kuring.auth.handler.LoginAuthenticationSuccessHandler;
import com.kustacks.kuring.auth.interceptor.AdminTokenAuthenticationFilter;
import com.kustacks.kuring.auth.interceptor.BearerTokenAuthenticationFilter;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final AdminDetailsService adminDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityContextPersistenceFilter());

        registry.addInterceptor(new AdminTokenAuthenticationFilter(
                adminDetailsService, passwordEncoder(), jwtTokenProvider, objectMapper,
                adminLoginSuccessHandler(), adminLoginFailureHandler()))
                .addPathPatterns("/api/v2/admin/login");

        registry.addInterceptor(new BearerTokenAuthenticationFilter(jwtTokenProvider)).addPathPatterns("/api/v2/admin/login/request");
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
}

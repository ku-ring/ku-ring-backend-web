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
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
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

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "https://www.ku-ring.com",
                "https://ku-ring.com",
                "http://localhost:[*]",
                "http://127.0.0.1:[*]"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // 모든 요청 헤더 허용

        // [보안 권장] 모든 응답 헤더를 노출하는 대신 필요한 헤더만 명시적으로 노출합니다.
        // 예를 들어, 프론트에서 Authorization 헤더에 담긴 토큰을 읽어야 할 경우 아래와 같이 설정합니다.
        config.setExposedHeaders(List.of("Authorization", "Location"));

        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // [수정] 필터 순서를 가장 높게 설정하여 다른 필터보다 먼저 실행되도록 합니다.
        return registration;
    }

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
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
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

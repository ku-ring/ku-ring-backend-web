package com.kustacks.kuring.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfiguration {

    private static final String FIREBASE_HEADER = "User-Token";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("JWT", JwtAuth())
                .addSecuritySchemes("User-Token", firebaseAuth())
            ).info(getInfo());
    }

    private static Info getInfo() {
        return new Info()
            .title("Kuring Backend API Docs")
            .description("Kuring의 Swagger API 문서입니다.")
            .version("2.5.0")
            .contact(new Contact().name("Kuring Contact")
                .url("https://kuring.notion.site/kuring/a69fdf7ff06848c2aedef1fdcf13ca57"));
    }

    private static SecurityScheme firebaseAuth() {
        return new SecurityScheme()
            .type(Type.APIKEY)
            .in(In.HEADER)
            .name(FIREBASE_HEADER);
    }

    private static SecurityScheme JwtAuth() {
        return new SecurityScheme()
            .type(Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(In.HEADER)
            .name(HttpHeaders.AUTHORIZATION);
    }
}

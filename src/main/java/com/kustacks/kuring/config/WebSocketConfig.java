package com.kustacks.kuring.config;

import com.kustacks.kuring.controller.handler.FrontWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final FrontWebSocketHandler frontWebSocketHandler;

    public WebSocketConfig(FrontWebSocketHandler frontWebSocketHandler) {
        this.frontWebSocketHandler = frontWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(frontWebSocketHandler, "/kuring/search")
                .setAllowedOrigins("https://kuring-dev.herokuapp.com")
                .setAllowedOrigins("https://kuring.herokuapp.com")
                .setAllowedOrigins("http://localhost:8080");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {

        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(-1L);
        return container;
    }
}

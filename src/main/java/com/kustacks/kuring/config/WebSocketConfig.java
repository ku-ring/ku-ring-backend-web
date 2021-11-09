package com.kustacks.kuring.config;

import com.kustacks.kuring.controller.StaffWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler staffWebSocketHandler;

    public WebSocketConfig(StaffWebSocketHandler staffWebSocketHandler) {
        this.staffWebSocketHandler = staffWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(staffWebSocketHandler, "/kuring/staff")
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

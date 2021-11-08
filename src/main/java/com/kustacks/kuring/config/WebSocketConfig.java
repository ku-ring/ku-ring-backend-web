package com.kustacks.kuring.config;

import com.kustacks.kuring.controller.StaffWebSocketHandler;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

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
}

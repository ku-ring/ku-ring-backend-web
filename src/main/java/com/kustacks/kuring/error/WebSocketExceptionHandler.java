package com.kustacks.kuring.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component
public class WebSocketExceptionHandler {

    private final ObjectMapper objectMapper;

    public WebSocketExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void sendErrorMessage(WebSocketSession session, ErrorCode e) throws IOException {

        log.error("[WebSocketException] {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e);
        String errorResponseString = objectMapper.writeValueAsString(errorResponse);
        session.sendMessage(new TextMessage(errorResponseString));
    }
}

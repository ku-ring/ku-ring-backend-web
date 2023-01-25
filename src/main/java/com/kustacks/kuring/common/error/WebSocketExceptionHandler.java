package com.kustacks.kuring.common.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
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

    public void sendErrorMessage(WebSocketSession session, ErrorCode errorCode, String type) {

        log.error("[WebSocketExceptionHandler] {}", errorCode.getMessage());
        Sentry.captureMessage(errorCode.getMessage());

        ErrorResponse errorResponse;
        if(errorCode.equals(ErrorCode.WS_CANNOT_STRINGIFY) || errorCode.equals(ErrorCode.WS_CANNOT_SEND)) {
            errorResponse = new WebSocketErrorResponse(ErrorCode.WS_SERVER_ERROR, type);
        } else {
            errorResponse = new WebSocketErrorResponse(errorCode, type);
        }

        try {
            String errorResponseString = objectMapper.writeValueAsString(errorResponse);
            session.sendMessage(new TextMessage(errorResponseString));
        } catch(IOException e) {
            if(e instanceof JsonProcessingException) {
                log.error("[WebSocketExceptionHandler] 에러 메세지 객체를 JSON형식 문자열로 변환 중 오류가 발생했습니다.");
                Sentry.captureMessage("[WebSocketExceptionHandler] 에러 메세지 객체를 JSON형식 문자열로 변환 중 오류가 발생했습니다.");
            } else {
                log.error("[WebSocketExceptionHandler] 에러 메세지 전송 중 오류가 발생했습니다.");
                Sentry.captureMessage("[WebSocketExceptionHandler] 에러 메세지 전송 중 오류가 발생했습니다.");
            }
        }
    }
}

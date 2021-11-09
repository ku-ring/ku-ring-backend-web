package com.kustacks.kuring.controller.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.controller.dto.SearchRequestDTO;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.WebSocketExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FrontWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketExceptionHandler exceptionHandler;

    private final NoticeWebSocketHandler noticeWebSocketHandler;
    private final StaffWebSocketHandler staffWebSocketHandler;

    private final Map<String, SearchHandler> supportedSearchTarget;

    public FrontWebSocketHandler(
            ObjectMapper objectMapper,
            WebSocketExceptionHandler exceptionHandler,
            NoticeWebSocketHandler noticeWebSocketHandler,
            StaffWebSocketHandler staffWebSocketHandler) {

        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;

        this.noticeWebSocketHandler = noticeWebSocketHandler;
        this.staffWebSocketHandler = staffWebSocketHandler;

        this.supportedSearchTarget = new HashMap<>();
        supportedSearchTarget.put("notice", noticeWebSocketHandler);
        supportedSearchTarget.put("staff", staffWebSocketHandler);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        String payload = message.getPayload();
        SearchRequestDTO requestDTO;

        try {
            requestDTO = objectMapper.readValue(payload, SearchRequestDTO.class);
        } catch(IOException e) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_PARSE_JSON);
            log.error("", e);
            return;
        }

        String type = requestDTO.getType();
        String content = requestDTO.getContent();

        if(type == null || content == null) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_MISSING_PARAM);
            return;
        }

        SearchHandler searchHandler = supportedSearchTarget.get(type);
        if(searchHandler == null) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_INVALID_PARAM);
            return;
        }

        searchHandler.handleTextMessage(session, content);
    }

    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.debug(session + " 클라이언트 접속");
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug(session + " 클라이언트 접속 해제");
    }
}

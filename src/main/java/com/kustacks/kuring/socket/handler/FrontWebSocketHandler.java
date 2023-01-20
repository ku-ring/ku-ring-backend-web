package com.kustacks.kuring.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.common.dto.HeartBeatResponseDto;
import com.kustacks.kuring.common.dto.SearchRequestDto;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.WebSocketExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FrontWebSocketHandler extends TextWebSocketHandler {

    private final String ERROR_TYPE = "unknown";

    private final ObjectMapper objectMapper;
    private final WebSocketExceptionHandler exceptionHandler;

    private final NoticeWebSocketHandler noticeWebSocketHandler;
    private final StaffWebSocketHandler staffWebSocketHandler;

    private final Map<String, SearchHandler> supportedHandlers;

    public FrontWebSocketHandler(
            ObjectMapper objectMapper,
            WebSocketExceptionHandler exceptionHandler,
            NoticeWebSocketHandler noticeWebSocketHandler,
            StaffWebSocketHandler staffWebSocketHandler) {

        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;

        this.noticeWebSocketHandler = noticeWebSocketHandler;
        this.staffWebSocketHandler = staffWebSocketHandler;

        this.supportedHandlers = new HashMap<>();
        supportedHandlers.put("notice", noticeWebSocketHandler);
        supportedHandlers.put("staff", staffWebSocketHandler);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        String payload = message.getPayload();
        SearchRequestDto requestDTO;

        try {
            requestDTO = objectMapper.readValue(payload, SearchRequestDto.class);
        } catch(IOException e) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_PARSE_JSON, ERROR_TYPE);
            log.error("", e);
            return;
        }

        String type = requestDTO.getType();
        String content = requestDTO.getContent();

        if(type == null || content == null) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_MISSING_PARAM, ERROR_TYPE);
            return;
        }



        if(type.equals("heartbeat")) {
            try {
                handleHeartBeat(session);
            } catch(IOException e) {
                log.error("[FrontWebSocketHandler] 클라이언트에게 heartbeat 전송 실패", e);
            }
            return;
        }

        SearchHandler searchHandler = supportedHandlers.get(type);
        if(searchHandler == null) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_INVALID_PARAM, ERROR_TYPE);
            return;
        }

        searchHandler.handleTextMessage(session, content);
    }

    private void handleHeartBeat(WebSocketSession session) throws IOException {

        HeartBeatResponseDto responseDTO = new HeartBeatResponseDto("heartbeat", LocalDateTime.now().toString());

        String responseString = objectMapper.writeValueAsString(responseDTO);
        session.sendMessage(new TextMessage(responseString));
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

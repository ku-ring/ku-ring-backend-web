package com.kustacks.kuring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.controller.dto.NoticeSearchDTO;
import com.kustacks.kuring.controller.dto.NoticeWebSocketRequestDTO;
import com.kustacks.kuring.controller.dto.NoticeWebSocketResponseDTO;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.WebSocketExceptionHandler;
import com.kustacks.kuring.service.NoticeService;
import com.kustacks.kuring.service.NoticeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class NoticeWebSocketHandler extends TextWebSocketHandler {

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    private final NoticeService noticeService;
    private final ObjectMapper objectMapper;
    private final WebSocketExceptionHandler exceptionHandler;
    private final List<String> supportedTypes;

    public NoticeWebSocketHandler(
            NoticeServiceImpl noticeService,
            ObjectMapper objectMapper,
            WebSocketExceptionHandler exceptionHandler) {

        this.noticeService = noticeService;
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;

        supportedTypes = new LinkedList<>();
        supportedTypes.add("search");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        String payload = message.getPayload();
        NoticeWebSocketRequestDTO requestDTO;

        try {
            requestDTO = objectMapper.readValue(payload, NoticeWebSocketRequestDTO.class);
        } catch(IOException e) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_SERVER_ERROR);
            return;
        }

        String type = requestDTO.getType();
        String keywords = requestDTO.getContent();

        if(type == null || keywords == null) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_MISSING_PARAM);
            return;
        }

        if(!supportedTypes.contains(type) || keywords.length() == 0) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_INVALID_PARAM);
        } else {
            List<Notice> notices = noticeService.handleSearchRequest(keywords);

            LinkedList<NoticeSearchDTO> noticeDTOList = new LinkedList<>();
            for (Notice notice : notices) {
                noticeDTOList.add(NoticeSearchDTO.entityToDTO(notice, notice.getCategory().getName().equals("library")
                                ? libraryBaseUrl : normalBaseUrl));
            }

            NoticeWebSocketResponseDTO responseObject = NoticeWebSocketResponseDTO.builder().noticeList(noticeDTOList).build();

            try {
                String responseString = objectMapper.writeValueAsString(responseObject);
                session.sendMessage(new TextMessage(responseString));
            } catch(IOException e) {
                if(e instanceof JsonProcessingException) {
                    exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_STRINGIFY);
                } else {
                    exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_SEND);
                }
            }
        }


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

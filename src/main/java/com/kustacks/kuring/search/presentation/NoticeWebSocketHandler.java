package com.kustacks.kuring.search.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.WebSocketExceptionHandler;
import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.search.common.dto.NoticeSearchDto;
import com.kustacks.kuring.search.common.dto.NoticeWebSocketResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class NoticeWebSocketHandler implements SearchHandler {

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    private final String ERROR_TYPE = "notice";

    private final NoticeService noticeService;
    private final ObjectMapper objectMapper;
    private final WebSocketExceptionHandler exceptionHandler;


    public NoticeWebSocketHandler(
            NoticeService noticeService,
            ObjectMapper objectMapper,
            WebSocketExceptionHandler exceptionHandler) {

        this.noticeService = noticeService;
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, String keywords) {

        if(keywords.length() == 0) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_INVALID_PARAM, ERROR_TYPE);
            return;
        }

        List<NoticeSearchDto> noticeDtoList = noticeService.handleSearchRequest(keywords);

        NoticeWebSocketResponseDto responseObject = new NoticeWebSocketResponseDto(noticeDtoList);

        try {
            String responseString = objectMapper.writeValueAsString(responseObject);
            session.sendMessage(new TextMessage(responseString));
        } catch(IOException e) {
            if(e instanceof JsonProcessingException) {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_STRINGIFY, ERROR_TYPE);
            } else {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_SEND, ERROR_TYPE);
            }
            log.error("", e);
        }
    }
}

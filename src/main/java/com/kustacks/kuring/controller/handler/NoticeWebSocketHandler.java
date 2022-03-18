package com.kustacks.kuring.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.controller.dto.NoticeSearchDTO;
import com.kustacks.kuring.controller.dto.NoticeWebSocketResponseDTO;
import com.kustacks.kuring.persistence.notice.Notice;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.WebSocketExceptionHandler;
import com.kustacks.kuring.service.NoticeService;
import com.kustacks.kuring.service.NoticeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
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
            NoticeServiceImpl noticeService,
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
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_STRINGIFY, ERROR_TYPE);
            } else {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_SEND, ERROR_TYPE);
            }
            log.error("", e);
        }
    }
}

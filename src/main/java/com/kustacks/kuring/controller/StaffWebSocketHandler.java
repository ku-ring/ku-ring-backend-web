package com.kustacks.kuring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.controller.dto.StaffDTO;
import com.kustacks.kuring.controller.dto.StaffWebSocketRequestDTO;
import com.kustacks.kuring.controller.dto.StaffWebSocketResponseDTO;
import com.kustacks.kuring.domain.staff.Staff;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.error.WebSocketExceptionHandler;
import com.kustacks.kuring.service.StaffService;
import com.kustacks.kuring.service.StaffServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class StaffWebSocketHandler extends TextWebSocketHandler {

    private final StaffService staffService;
    private final ObjectMapper objectMapper;
    private final WebSocketExceptionHandler exceptionHandler;
    private final List<String> supportedTypes;

    public StaffWebSocketHandler(
            StaffServiceImpl staffService,
            ObjectMapper objectMapper,
            WebSocketExceptionHandler exceptionHandler) {

        this.staffService = staffService;
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;

        supportedTypes = new ArrayList<>();
        supportedTypes.add("search");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {

        String payload = message.getPayload();
        StaffWebSocketRequestDTO requestDTO;

        try {
            requestDTO = objectMapper.readValue(payload, StaffWebSocketRequestDTO.class);
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

            List<Staff> searchResult = staffService.handleSearchRequest(keywords);

            List<StaffDTO> searchResultDTOList = new LinkedList<>();
            for (Staff staff : searchResult) {
                searchResultDTOList.add(StaffDTO.entityToDTO(staff));
            }

            StaffWebSocketResponseDTO responseObject = StaffWebSocketResponseDTO.builder()
                    .staffDTOList(searchResultDTOList).build();

            try {
                String responseString = objectMapper.writeValueAsString(responseObject);
                session.sendMessage(new TextMessage(responseString));
            } catch(IOException e) {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_SERVER_ERROR);
            }
        }
    }

    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug(session + " 클라이언트 접속");
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug(session + " 클라이언트 접속 해제");
    }
}

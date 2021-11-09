package com.kustacks.kuring.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.controller.dto.StaffDTO;
import com.kustacks.kuring.controller.dto.StaffWebSocketResponseDTO;
import com.kustacks.kuring.domain.staff.Staff;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.WebSocketExceptionHandler;
import com.kustacks.kuring.service.StaffService;
import com.kustacks.kuring.service.StaffServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class StaffWebSocketHandler implements SearchHandler {

    private final StaffService staffService;
    private final ObjectMapper objectMapper;
    private final WebSocketExceptionHandler exceptionHandler;

    public StaffWebSocketHandler(
            StaffServiceImpl staffService,
            ObjectMapper objectMapper,
            WebSocketExceptionHandler exceptionHandler) {

        this.staffService = staffService;
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, String keywords) {

        if(keywords.length() == 0) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_INVALID_PARAM);
            return;
        }

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
            if(e instanceof JsonProcessingException) {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_STRINGIFY);
            } else {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_SERVER_ERROR);
            }
            log.error("", e);
        }
    }
}

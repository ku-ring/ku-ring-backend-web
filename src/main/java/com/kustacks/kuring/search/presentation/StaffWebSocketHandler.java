package com.kustacks.kuring.search.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.common.dto.StaffDto;
import com.kustacks.kuring.search.common.dto.response.StaffWebSocketResponseDto;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.WebSocketExceptionHandler;
import com.kustacks.kuring.staff.business.StaffService;
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

    private final String ERROR_TYPE = "staff";

    private final StaffService staffService;
    private final ObjectMapper objectMapper;
    private final WebSocketExceptionHandler exceptionHandler;

    public StaffWebSocketHandler(
            StaffService staffService,
            ObjectMapper objectMapper,
            WebSocketExceptionHandler exceptionHandler) {

        this.staffService = staffService;
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, String keywords) {

        if(keywords.length() == 0) {
            exceptionHandler.sendErrorMessage(session, ErrorCode.WS_INVALID_PARAM, ERROR_TYPE);
            return;
        }

        List<Staff> searchResult = staffService.handleSearchRequest(keywords);

        List<StaffDto> searchResultDTOList = new LinkedList<>();
        for (Staff staff : searchResult) {
            searchResultDTOList.add(StaffDto.entityToDto(staff));
        }

        StaffWebSocketResponseDto responseObject = new StaffWebSocketResponseDto(searchResultDTOList);

        try {
            String responseString = objectMapper.writeValueAsString(responseObject);
            session.sendMessage(new TextMessage(responseString));
        } catch(IOException e) {
            if(e instanceof JsonProcessingException) {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_CANNOT_STRINGIFY, ERROR_TYPE);
            } else {
                exceptionHandler.sendErrorMessage(session, ErrorCode.WS_SERVER_ERROR, ERROR_TYPE);
            }
            log.error("", e);
        }
    }
}

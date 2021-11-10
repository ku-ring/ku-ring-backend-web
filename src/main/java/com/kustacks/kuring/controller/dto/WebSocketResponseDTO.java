package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class WebSocketResponseDTO extends ResponseDTO {

    @JsonProperty("type")
    private String type;

    public WebSocketResponseDTO(boolean isSuccess, String resultMsg, int resultCode, String type) {
        super(isSuccess, resultMsg, resultCode);
        this.type = type;
    }
}

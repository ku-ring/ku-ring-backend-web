package com.kustacks.kuring.common.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class WebSocketErrorResponse extends ErrorResponse {

    @JsonProperty("type")
    private String type;

    public WebSocketErrorResponse(ErrorCode errorCode, String type) {
        super(errorCode);
        this.type = type;
    }
}

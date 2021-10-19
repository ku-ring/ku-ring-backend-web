package com.kustacks.kuring.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @JsonProperty("isSuccess")
    private final boolean success = false;

    @JsonProperty("resultMsg")
    private String resultMsg;

    @JsonProperty("resultCode")
    private int resultCode;

    public ErrorResponse(ErrorCode errorCode) {
        this.resultMsg = errorCode.getMessage();
        this.resultCode = errorCode.getHttpStatus() == null ? 500 : errorCode.getHttpStatus().value();
    }
}

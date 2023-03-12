package com.kustacks.kuring.common.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @JsonProperty("isSuccess")
    private final boolean success = false;

    private String resultMsg;

    private int resultCode;

    public ErrorResponse(ErrorCode errorCode) {
        this.resultMsg = errorCode.getMessage();
        this.resultCode = errorCode.getHttpStatus() == null ? 500 : errorCode.getHttpStatus().value();
    }
}

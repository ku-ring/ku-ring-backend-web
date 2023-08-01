package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustacks.kuring.common.exception.code.ErrorCode;
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

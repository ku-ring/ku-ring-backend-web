package com.kustacks.kuring.common.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException {
    private final ErrorCode errorCode;

    public InfrastructureException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public InfrastructureException(ErrorCode errorCode, Exception e) {
        super(e);
        this.errorCode = errorCode;
    }
}

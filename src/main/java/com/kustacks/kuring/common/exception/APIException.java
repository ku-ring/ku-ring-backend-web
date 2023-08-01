package com.kustacks.kuring.common.exception;

public class APIException extends BusinessException {
    public APIException(ErrorCode errorCode) {
        super(errorCode);
    }

    public APIException(ErrorCode errorCode, Exception e) {
        super(errorCode, e);
    }
}

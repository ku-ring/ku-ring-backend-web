package com.kustacks.kuring.error;

public class APIException extends BusinessException {
    public APIException(ErrorCode errorCode) {
        super(errorCode);
    }

    public APIException(ErrorCode errorCode, Exception e) {
        super(errorCode, e);
    }
}

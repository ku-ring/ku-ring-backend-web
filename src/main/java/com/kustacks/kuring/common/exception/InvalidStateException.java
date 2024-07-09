package com.kustacks.kuring.common.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class InvalidStateException extends BusinessException {

    public InvalidStateException(ErrorCode errorCode) {
        super(errorCode);
    }
    public InvalidStateException(ErrorCode errorCode, Exception e) {
        super(errorCode, e);
    }
}

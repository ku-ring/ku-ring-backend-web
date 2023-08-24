package com.kustacks.kuring.common.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class InternalLogicException extends BusinessException {
    public InternalLogicException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InternalLogicException(ErrorCode errorCode, Exception e) {
        super(errorCode, e);
    }
}

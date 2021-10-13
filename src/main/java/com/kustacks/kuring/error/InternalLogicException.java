package com.kustacks.kuring.error;

public class InternalLogicException extends BusinessException {
    public InternalLogicException(ErrorCode errorCode) {
        super(errorCode);
    }
}

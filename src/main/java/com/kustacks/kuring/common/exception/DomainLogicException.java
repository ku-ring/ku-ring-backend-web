package com.kustacks.kuring.common.exception;

public class DomainLogicException extends BusinessException {

    public DomainLogicException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DomainLogicException(ErrorCode errorCode, Exception e) {
        super(errorCode, e);
    }
}

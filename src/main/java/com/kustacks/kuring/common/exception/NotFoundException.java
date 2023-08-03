package com.kustacks.kuring.common.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

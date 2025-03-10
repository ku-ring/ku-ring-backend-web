package com.kustacks.kuring.common.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class NoPermissionException extends BusinessException {

    public NoPermissionException(ErrorCode errorCode) {
        super(errorCode);
    }
}

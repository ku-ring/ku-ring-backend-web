package com.kustacks.kuring.email.application.service.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class EmailBusinessException extends BusinessException {
    public EmailBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package com.kustacks.kuring.calendar.application.service.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class AcademicEventException extends BusinessException {
    public AcademicEventException(ErrorCode errorCode) {
        super(errorCode);
    }
}

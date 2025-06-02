package com.kustacks.kuring.report.application.service.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class ReportBusinessException extends BusinessException {
    public ReportBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}

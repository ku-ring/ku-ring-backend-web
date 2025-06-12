package com.kustacks.kuring.common.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class BadWordContainsException extends BusinessException {

    public BadWordContainsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package com.kustacks.kuring.message.firebase.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class FirebaseBusinessException extends BusinessException {
    public FirebaseBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}

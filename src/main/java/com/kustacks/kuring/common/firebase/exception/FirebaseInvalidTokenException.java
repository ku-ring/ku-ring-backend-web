package com.kustacks.kuring.common.firebase.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class FirebaseInvalidTokenException extends BusinessException {

    public FirebaseInvalidTokenException() {
        super(ErrorCode.API_FB_INVALID_TOKEN);
    }
}

package com.kustacks.kuring.common.firebase.exception;

import com.kustacks.kuring.common.error.BusinessException;
import com.kustacks.kuring.common.error.ErrorCode;

public class FirebaseInvalidTokenException extends BusinessException {

    public FirebaseInvalidTokenException() {
        super(ErrorCode.API_FB_INVALID_TOKEN);
    }
}

package com.kustacks.kuring.message.application.service.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class FirebaseInvalidTokenException extends FirebaseBusinessException {

    public FirebaseInvalidTokenException() {
        super(ErrorCode.API_FB_INVALID_TOKEN);
    }
}

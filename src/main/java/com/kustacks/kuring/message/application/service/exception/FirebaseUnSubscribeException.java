package com.kustacks.kuring.message.application.service.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class FirebaseUnSubscribeException extends FirebaseBusinessException {

    public FirebaseUnSubscribeException() {
        super(ErrorCode.FB_FAIL_UNSUBSCRIBE);
    }
}

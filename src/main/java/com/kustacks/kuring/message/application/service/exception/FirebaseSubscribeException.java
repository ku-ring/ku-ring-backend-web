package com.kustacks.kuring.message.application.service.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class FirebaseSubscribeException extends FirebaseBusinessException {

    public FirebaseSubscribeException() {
        super(ErrorCode.FB_FAIL_SUBSCRIBE);
    }
}

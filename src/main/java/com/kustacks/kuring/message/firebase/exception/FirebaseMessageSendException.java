package com.kustacks.kuring.message.firebase.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class FirebaseMessageSendException extends FirebaseBusinessException {

    public FirebaseMessageSendException() {
        super(ErrorCode.FB_FAIL_SEND);
    }
}

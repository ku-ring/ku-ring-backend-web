package com.kustacks.kuring.message.firebase.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.firebase.FirebaseBusinessException;

public class FirebaseMessageSendException extends FirebaseBusinessException {

    public FirebaseMessageSendException() {
        super(ErrorCode.FB_FAIL_SEND);
    }
}

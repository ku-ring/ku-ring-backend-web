package com.kustacks.kuring.message.firebase.exception;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.firebase.FirebaseBusinessException;

public class FirebaseSubscribeException extends FirebaseBusinessException {

    public FirebaseSubscribeException() {
        super(ErrorCode.FB_FAIL_SUBSCRIBE);
    }
}

package com.kustacks.kuring.message.firebase.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class FirebaseSubscribeException extends BusinessException {

    public FirebaseSubscribeException() {
        super(ErrorCode.FB_FAIL_SUBSCRIBE);
    }
}

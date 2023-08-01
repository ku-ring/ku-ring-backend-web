package com.kustacks.kuring.message.firebase.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class FirebaseUnSubscribeException extends BusinessException {

    public FirebaseUnSubscribeException() {
        super(ErrorCode.FB_FAIL_UNSUBSCRIBE);
    }
}

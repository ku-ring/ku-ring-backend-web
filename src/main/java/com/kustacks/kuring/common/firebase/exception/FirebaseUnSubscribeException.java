package com.kustacks.kuring.common.firebase.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.ErrorCode;

public class FirebaseUnSubscribeException extends BusinessException {

    public FirebaseUnSubscribeException() {
        super(ErrorCode.FB_FAIL_UNSUBSCRIBE);
    }
}

package com.kustacks.kuring.common.firebase.exception;

import com.kustacks.kuring.common.error.BusinessException;
import com.kustacks.kuring.common.error.ErrorCode;

public class FirebaseUnSubscribeException extends BusinessException {

    public FirebaseUnSubscribeException() {
        super(ErrorCode.FB_FAIL_UNSUBSCRIBE);
    }
}

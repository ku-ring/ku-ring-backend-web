package com.kustacks.kuring.common.firebase.exception;

import com.kustacks.kuring.common.error.BusinessException;
import com.kustacks.kuring.common.error.ErrorCode;

public class FirebaseSubscribeException extends BusinessException {

    public FirebaseSubscribeException() {
        super(ErrorCode.FB_FAIL_SUBSCRIBE);
    }
}

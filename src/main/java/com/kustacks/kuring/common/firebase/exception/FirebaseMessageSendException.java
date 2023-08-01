package com.kustacks.kuring.common.firebase.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.ErrorCode;

public class FirebaseMessageSendException extends BusinessException {

    public FirebaseMessageSendException() {
        super(ErrorCode.FB_FAIL_SEND);
    }
}

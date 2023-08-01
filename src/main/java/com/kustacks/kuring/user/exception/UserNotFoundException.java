package com.kustacks.kuring.user.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}

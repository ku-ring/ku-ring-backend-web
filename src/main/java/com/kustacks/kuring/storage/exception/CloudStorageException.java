package com.kustacks.kuring.storage.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class CloudStorageException extends BusinessException {

    public CloudStorageException(ErrorCode errorCode) {
        super(errorCode);
    }
}

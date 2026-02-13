package com.kustacks.kuring.storage.exception;

import com.kustacks.kuring.common.exception.InfrastructureException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public class CloudStorageException extends InfrastructureException {

    public CloudStorageException(ErrorCode errorCode) {
        super(errorCode);
    }
}

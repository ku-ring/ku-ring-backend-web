package com.kustacks.kuring.new_message.exception.message;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class MessageBusinessException extends BusinessException {
    public MessageBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}

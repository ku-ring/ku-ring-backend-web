package com.kustacks.kuring.new_message.exception.message;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class MessageSubscribeException extends MessageBusinessException {

    public MessageSubscribeException() {
        super(ErrorCode.MESSAGE_SUBSCRIPTION_FAILED);
    }

}
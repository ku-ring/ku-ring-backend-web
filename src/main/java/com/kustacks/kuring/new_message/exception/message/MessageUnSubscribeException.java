package com.kustacks.kuring.new_message.exception.message;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class MessageUnSubscribeException extends MessageBusinessException {

    public MessageUnSubscribeException() {
        super(ErrorCode.MESSAGE_UNSUBSCRIPTION_FAILED);
    }

}

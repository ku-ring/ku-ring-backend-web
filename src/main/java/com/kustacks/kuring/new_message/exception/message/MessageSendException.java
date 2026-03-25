package com.kustacks.kuring.new_message.exception.message;

import com.kustacks.kuring.common.exception.code.ErrorCode;

public class MessageSendException extends MessageBusinessException {

    public MessageSendException() {
        super(ErrorCode.MESSAGE_SEND_FAILED);
    }

    public MessageSendException(Exception e) {
        super(ErrorCode.MESSAGE_SEND_FAILED, e);
    }

}

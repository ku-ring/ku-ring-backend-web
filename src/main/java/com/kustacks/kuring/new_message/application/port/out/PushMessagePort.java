package com.kustacks.kuring.new_message.application.port.out;

import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.exception.message.MessageSendException;

public interface PushMessagePort {
    void send(NotificationCommand command) throws MessageSendException;
}

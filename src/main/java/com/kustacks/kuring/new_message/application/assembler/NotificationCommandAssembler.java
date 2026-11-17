package com.kustacks.kuring.new_message.application.assembler;

import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;

import java.util.List;

public interface NotificationCommandAssembler<E extends MessageEvent> {

    Class<E> supportEventType();

    List<NotificationCommand> assemble(E event);

}

package com.kustacks.kuring.new_message.application.port.in;

import com.kustacks.kuring.new_message.domain.event.MessageEvent;

public interface HandleMessageEventUseCase {
    int handle(MessageEvent event);
}

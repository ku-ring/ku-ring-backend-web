package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.domain.event.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewMessageEventListener {

    private final HandleMessageEventUseCase handleMessageEventUseCase;

    @Async
    @EventListener
    public void sendNotification(MessageEvent event) {
        handleMessageEventUseCase.handle(event);
    }

}

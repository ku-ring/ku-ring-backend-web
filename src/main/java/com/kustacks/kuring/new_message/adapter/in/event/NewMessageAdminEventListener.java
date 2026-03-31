package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.domain.event.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewMessageAdminEventListener {

    private final HandleMessageEventUseCase handleMessageEventUseCase;

    @Async
    @EventListener
    public void sendAdminNotification(AdminNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    public void sendAdminTestNotification(AdminTestNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    public void sendAlert(AlertSendEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    public void sendAcademicTestNotification(AcademicTestNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }

}

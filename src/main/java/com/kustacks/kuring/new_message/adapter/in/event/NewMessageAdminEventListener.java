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
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0),
            recover = "recoverAdminNotification"
    )
    public void sendAdminNotification(AdminNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0),
            recover = "recoverAdminTestNotification"
    )
    public void sendAdminTestNotification(AdminTestNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0),
            recover = "recoverAlert"
    )
    public void sendAlert(AlertSendEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0),
            recover = "recoverAcademicTestNotification"
    )
    public void sendAcademicTestNotification(AcademicTestNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }


    @Recover
    public void recoverAdminNotification(Exception e, AdminNotificationEvent event) {
        log.error("AdminNotification 전송 최종 실패. event={}, message={}", event, e.getMessage(), e);
    }

    @Recover
    public void recoverAdminTestNotification(Exception e, AdminTestNotificationEvent event) {
        log.error("AdminTestNotification 전송 최종 실패. event={}, message={}", event, e.getMessage(), e);
    }

    @Recover
    public void recoverAlert(Exception e, AlertSendEvent event) {
        log.error("Alert 전송 최종 실패. event={}, message={}", event, e.getMessage(), e);
    }

    @Recover
    public void recoverAcademicTestNotification(Exception e, AcademicTestNotificationEvent event) {
        log.error("AcademicTestNotification 전송 최종 실패. event={}, message={}", event, e.getMessage(), e);
    }

}

package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.domain.event.AcademicScheduleNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.ClubDeadlineNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.NoticeBatchNotificationEvent;

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
public class NewMessageDomainEventListener {

    private final HandleMessageEventUseCase handleMessageEventUseCase;

    @Async
    @EventListener
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0),
            recover = "recoverAcademicScheduleNotification"
    )
    public void sendAcademicScheduleNotification(AcademicScheduleNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0),
            recover = "recoverClubDeadlineNotification"
    )
    public void sendClubDeadlineNotification(ClubDeadlineNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }

    @Async
    @EventListener
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000, multiplier = 2.0),
            recover = "recoverNoticeBatchNotification"
    )
    public void sendNoticeBatchNotification(NoticeBatchNotificationEvent event) {
        handleMessageEventUseCase.handle(event);
    }


    @Recover
    public void recoverAcademicScheduleNotification(Exception e, AcademicScheduleNotificationEvent event) {
        log.error("AcademicScheduleNotification 전송 최종 실패. event={}, message={}", event, e.getMessage(), e);
    }

    @Recover
    public void recoverClubDeadlineNotification(Exception e, ClubDeadlineNotificationEvent event) {
        log.error("ClubDeadlineNotification 전송 최종 실패. event={}, message={}", event, e.getMessage(), e);
    }

    @Recover
    public void recoverNoticeBatchNotification(Exception e, NoticeBatchNotificationEvent event) {
        log.error("NoticeBatchNotification 전송 최종 실패. event={}, message={}", event, e.getMessage(), e);
    }
}

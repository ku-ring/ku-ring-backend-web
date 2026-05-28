package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.ManageTopicSubscriptionUseCase;
import com.kustacks.kuring.new_message.domain.event.UserSubscribeEvent;
import com.kustacks.kuring.new_message.domain.event.UserUnsubscribeEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewMessageUserEventListener {

    private final ManageTopicSubscriptionUseCase manageTopicSubscriptionUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void subscribeEvent(UserSubscribeEvent event) {
        manageTopicSubscriptionUseCase.subscribe(event.token(), event.topic());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void unsubscribeEvent(UserUnsubscribeEvent event) {
        manageTopicSubscriptionUseCase.unsubscribe(event.token(), event.topic());
    }

}

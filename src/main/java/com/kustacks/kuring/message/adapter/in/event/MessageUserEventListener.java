package com.kustacks.kuring.message.adapter.in.event;

import com.kustacks.kuring.message.adapter.in.event.dto.UserSubscribeEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.UserUnsubscribeEvent;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageUserEventListener {

    private final FirebaseWithUserUseCase firebaseWithUserUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void subscribeEvent(
            UserSubscribeEvent event
    ) {
        firebaseWithUserUseCase.subscribe(event.toCommand());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void unSubscribeEvent(
            UserUnsubscribeEvent event
    ) {
        firebaseWithUserUseCase.unsubscribe(event.toCommand());
    }
}

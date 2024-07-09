package com.kustacks.kuring.user.adapter.in.event;

import com.kustacks.kuring.user.adapter.in.event.dto.UserDecreaseQuestionCountEvent;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommandEventListener {

    private final UserCommandUseCase userCommandUseCase;

    @EventListener
    public void decreaseQuestionCountEvent(
            UserDecreaseQuestionCountEvent event
    ) {
        userCommandUseCase.decreaseQuestionCount(event.toCommand());
    }
}

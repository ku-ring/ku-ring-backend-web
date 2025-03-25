package com.kustacks.kuring.user.adapter.in.event;

import com.kustacks.kuring.ai.adapter.in.event.dto.RootUserDecreaseQuestionCountEvent;
import com.kustacks.kuring.user.adapter.in.event.dto.UserDecreaseQuestionCountEvent;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCommandEventListener {

    private final UserCommandUseCase userCommandUseCase;

    @EventListener
    public void decreaseUserQuestionCountEvent(
            UserDecreaseQuestionCountEvent event
    ) {
        userCommandUseCase.decreaseQuestionCount(event.toCommand());
    }

    @EventListener
    public void decreaseRootUserQuestionCountEvent(
            RootUserDecreaseQuestionCountEvent event
    ) {
        userCommandUseCase.decreaseQuestionCount(event.toCommand());
    }
}

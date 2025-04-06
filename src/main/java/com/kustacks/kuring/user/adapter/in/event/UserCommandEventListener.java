package com.kustacks.kuring.user.adapter.in.event;

import com.kustacks.kuring.user.adapter.in.event.dto.UserAskedQuestionEvent;
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
    public void userAskedQuestionEventHandler(
            UserAskedQuestionEvent event
    ) {
        userCommandUseCase.decreaseQuestionCount(event.toCommand());
    }
}

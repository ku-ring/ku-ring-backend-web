package com.kustacks.kuring.ai.adapter.out.event;

import com.kustacks.kuring.ai.adapter.in.event.dto.RootUserDecreaseQuestionCountEvent;
import com.kustacks.kuring.ai.application.port.out.RAGEventPort;
import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.user.adapter.in.event.dto.UserDecreaseQuestionCountEvent;
import org.springframework.stereotype.Component;

@Component
public class RAGEventAdapter implements RAGEventPort {

    @Override
    public void userDecreaseQuestionCountEvent(String userId) {
        Events.raise(new UserDecreaseQuestionCountEvent(userId));
    }

    @Override
    public void rootUserDecreaseQuestionCountEvent(String userId, String email) {
        Events.raise(new RootUserDecreaseQuestionCountEvent(userId, email));
    }
}

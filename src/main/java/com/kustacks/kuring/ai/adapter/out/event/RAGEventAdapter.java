package com.kustacks.kuring.ai.adapter.out.event;

import com.kustacks.kuring.ai.application.port.out.RAGEventPort;
import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.user.adapter.in.event.dto.UserAskedQuestionEvent;
import org.springframework.stereotype.Component;

@Component
public class RAGEventAdapter implements RAGEventPort {

    @Override
    public void askedQuestion(String userId, String email) {
        Events.raise(new UserAskedQuestionEvent(userId, email));
    }
}

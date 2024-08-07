package com.kustacks.kuring.user.adapter.out.event;

import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.message.adapter.in.event.dto.UserSubscribeEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.UserUnsubscribeEvent;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import org.springframework.stereotype.Component;

@Component
public class UserFirebaseMessageAdapter implements UserEventPort {

    @Override
    public void subscribeEvent(String token, String topic) throws FirebaseSubscribeException {
        Events.raise(new UserSubscribeEvent(token, topic));
    }

    @Override
    public void unsubscribeEvent(String token, String topic) throws FirebaseUnSubscribeException {
        Events.raise(new UserUnsubscribeEvent(token, topic));
    }
}

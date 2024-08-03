package com.kustacks.kuring.alert.adapter.out.event;

import com.kustacks.kuring.alert.application.port.out.AlertEventPort;
import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.message.adapter.in.event.dto.AlertSendEvent;
import org.springframework.stereotype.Component;

@Component
public class AlertFirebaseMessageAdapter implements AlertEventPort {

    @Override
    public void sendAlertEvent(String title, String content) {
        Events.raise(new AlertSendEvent(title, content));
    }
}

package com.kustacks.kuring.email.adapter.out.event;

import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.email.adapter.in.event.dto.EmailSendEvent;
import com.kustacks.kuring.email.application.port.out.EmailEventPort;
import org.springframework.stereotype.Component;

@Component
public class EmailEventAdapter implements EmailEventPort {

    @Override
    public void sendEmailEvent(String from, String to, String subject, String body) {
        Events.raise(new EmailSendEvent(from, to, subject, body));
    }
}

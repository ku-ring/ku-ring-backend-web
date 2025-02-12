package com.kustacks.kuring.email.adapter.in.event;

import com.kustacks.kuring.email.adapter.in.event.dto.EmailSendEvent;
import com.kustacks.kuring.email.application.port.in.EmailCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailCommandEventListener {
    private final EmailCommandUseCase emailCommandUseCase;
    
    @Async
    @EventListener
    public void sendEmail(
        EmailSendEvent emailSendEvent
    ) {
        emailCommandUseCase.sendEmail(emailSendEvent.toCommand());
    }

}

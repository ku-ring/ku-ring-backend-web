package com.kustacks.kuring.message.adapter.in.event;

import com.kustacks.kuring.message.adapter.in.event.dto.AdminNotificationEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.AdminTestNotificationEvent;
import com.kustacks.kuring.message.application.port.in.FirebaseWithAdminUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageAdminEventListener {

    private final FirebaseWithAdminUseCase firebaseWithAdminUseCase;

    @Async
    @EventListener
    public void sendNotificationEvent(
            AdminNotificationEvent event
    ) {
        firebaseWithAdminUseCase.sendNotificationByAdmin(event.toCommand());
    }

    @Async
    @EventListener
    public void sendTestNotificationEvent(
            AdminTestNotificationEvent event
    ) {
        firebaseWithAdminUseCase.sendTestNotificationByAdmin(event.toCommand());
    }
}

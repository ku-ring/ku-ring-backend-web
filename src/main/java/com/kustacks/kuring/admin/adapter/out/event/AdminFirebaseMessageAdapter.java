package com.kustacks.kuring.admin.adapter.out.event;

import com.kustacks.kuring.admin.application.port.out.AdminEventPort;
import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.message.adapter.in.event.dto.AcademicTestNotificationEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.AdminNotificationEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.AdminTestNotificationEvent;
import com.kustacks.kuring.message.application.service.exception.FirebaseMessageSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminFirebaseMessageAdapter implements AdminEventPort {

    @Override
    public void sendNotificationByAdmin(String title, String body, String url) {
        Events.raise(new AdminNotificationEvent(title, body, url));
    }

    @Override
    public void sendTestNotificationByAdmin(
            String articleId,
            String postedDate,
            String categoryName,
            String subject,
            String korName,
            String url
    ) throws FirebaseMessageSendException {
        Events.raise(
                AdminTestNotificationEvent.builder()
                        .articleId(articleId)
                        .postedDate(postedDate)
                        .category(categoryName)
                        .subject(subject)
                        .categoryKorName(korName)
                        .baseUrl(url)
                        .build()
        );
    }

    @Override
    public void sendAcademicTestNotification(String title, String body) {
        Events.raise(new AcademicTestNotificationEvent(title, body));
    }
}

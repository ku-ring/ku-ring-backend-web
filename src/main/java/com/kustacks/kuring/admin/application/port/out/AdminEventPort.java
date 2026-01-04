package com.kustacks.kuring.admin.application.port.out;

import com.kustacks.kuring.message.application.service.exception.FirebaseMessageSendException;

public interface AdminEventPort {
    void sendNotificationByAdmin(String title, String body, String url);

    void sendTestNotificationByAdmin(
            String articleId,
            String postedDate,
            String categoryName,
            String subject,
            String korName,
            String url
    ) throws FirebaseMessageSendException;

    void sendAcademicTestNotification(String title, String body);

}

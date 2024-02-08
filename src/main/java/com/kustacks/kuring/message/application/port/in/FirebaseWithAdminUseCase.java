package com.kustacks.kuring.message.application.port.in;

import com.kustacks.kuring.message.application.port.in.dto.AdminNotificationCommand;
import com.kustacks.kuring.message.application.port.in.dto.AdminTestNotificationCommand;

public interface FirebaseWithAdminUseCase {
    void sendNotificationByAdmin(AdminNotificationCommand command);
    void sendTestNotificationByAdmin(AdminTestNotificationCommand command);
}

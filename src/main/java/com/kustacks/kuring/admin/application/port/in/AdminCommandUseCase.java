package com.kustacks.kuring.admin.application.port.in;

import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;

public interface AdminCommandUseCase {
    void createTestNotice(TestNotificationCommand command);
    void createRealNoticeForAllUser(RealNotificationCommand command);
    void subscribeAllUserSameTopic();

    void addAlertSchedule(AlertCreateCommand command);

    void cancelAlertSchedule(Long id);
}

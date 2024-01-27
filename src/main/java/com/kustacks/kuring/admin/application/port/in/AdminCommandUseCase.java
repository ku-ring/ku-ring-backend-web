package com.kustacks.kuring.admin.application.port.in;

import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;

public interface AdminCommandUseCase {
    void createTestNotice(TestNotificationCommand command);
    void createRealNoticeForAllUser(RealNotificationCommand command);
    void subscribeAllUserSameTopic();
}

package com.kustacks.kuring.admin.application.port.in;

import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.alert.application.port.in.dto.DataEmbeddingCommand;

public interface AdminCommandUseCase {

    void createTestNotice(TestNotificationCommand command);

    void createRealNoticeForAllUser(RealNotificationCommand command);

    void addAlertSchedule(AlertCreateCommand command);

    void cancelAlertSchedule(Long id);

    void embeddingCustomData(DataEmbeddingCommand command);

    void resubscribeAllUsersToTopics();
}

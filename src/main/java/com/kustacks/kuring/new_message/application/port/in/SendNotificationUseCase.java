package com.kustacks.kuring.new_message.application.port.in;

import com.kustacks.kuring.new_message.domain.model.NotificationCommand;

import java.util.List;

public interface SendNotificationUseCase {

    boolean send(NotificationCommand command);

    int sendAll(List<NotificationCommand> commands);

}

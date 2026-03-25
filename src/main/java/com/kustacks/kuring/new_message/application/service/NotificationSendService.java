package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.application.port.in.SendNotificationUseCase;
import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.exception.message.MessageSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class NotificationSendService implements SendNotificationUseCase {

    private final PushMessagePort pushMessagePort;

    @Override
    public boolean send(NotificationCommand command) {
        try {
            pushMessagePort.send(command);
            return true;
        } catch (MessageSendException e) {
            log.error("푸시 알림 전송 실패. topic={}, title={}",
                    command.target().topic(), command.content().title(), e);
            return false;
        }
    }

    @Override
    public int sendAll(List<NotificationCommand> commands) {
        int successCount = 0;
        for (NotificationCommand command : commands) {
            boolean isSuccess = send(command);
            if (isSuccess) {
                successCount++;
            }
        }
        return successCount;
    }

}

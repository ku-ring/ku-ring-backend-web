package com.kustacks.kuring.new_message.application.assembler;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.event.AlertSendEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;

import java.util.List;
import java.util.Map;

@UseCase
public class AlertSendAssembler implements NotificationCommandAssembler<AlertSendEvent> {

    @Override
    public boolean supports(MessageEvent event) {
        return event instanceof AlertSendEvent;
    }

    @Override
    public List<NotificationCommand> assemble(AlertSendEvent event) {
        String title = event.title();
        String body = event.content();
        String topic = TopicNames.ALL_DEVICE_SUBSCRIBED_TOPIC;
        Map<String, String> data = Map.of(
                "type", MessageType.ADMIN.getValue(),
                "url", ""
        );

        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic(topic),
                NotificationContent.of(title, body),
                MessageType.ADMIN,
                data
        );

        return List.of(command);
    }

}

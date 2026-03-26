package com.kustacks.kuring.new_message.application.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.event.AdminNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@UseCase
@RequiredArgsConstructor
public class AdminNotificationAssembler implements NotificationCommandAssembler<AdminNotificationEvent> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MessageEvent event) {
        return event instanceof AdminNotificationEvent;
    }

    @Override
    public List<NotificationCommand> assemble(AdminNotificationEvent event) {
        String title = event.title();
        String body = event.body();
        String topic = TopicNames.ALL_DEVICE_SUBSCRIBED_TOPIC;
        Map<String, String> data = objectMapper.convertValue(event, Map.class);

        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic(topic),
                NotificationContent.of(title, body),
                MessageType.ADMIN,
                data
        );

        return List.of(command);
    }

}

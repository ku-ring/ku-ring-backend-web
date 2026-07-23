package com.kustacks.kuring.new_message.application.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.enums.TopicSuffixPolicy;
import com.kustacks.kuring.new_message.domain.event.AdminTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@UseCase
@RequiredArgsConstructor
public class AdminTestNotificationAssembler implements NotificationCommandAssembler<AdminTestNotificationEvent> {

    private static final String NOTIFICATION_TITLE = "새로운 공지가 왔어요!";
    private final ObjectMapper objectMapper;

    @Override
    public Class<AdminTestNotificationEvent> supportEventType() {
        return AdminTestNotificationEvent.class;
    }

    @Override
    public List<NotificationCommand> assemble(AdminTestNotificationEvent event) {
        String title = "[" + event.categoryKorName() + "] " + NOTIFICATION_TITLE;
        String body = event.subject();
        String topic = TopicNames.noticeTopic(event.category());
        Map<String, String> data = objectMapper.convertValue(event, Map.class);

        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic(topic, TopicSuffixPolicy.ALWAYS_ADD_DEV_SUFFIX),
                NotificationContent.of(title, body),
                MessageType.NOTICE,
                data
        );

        return List.of(command);
    }

}

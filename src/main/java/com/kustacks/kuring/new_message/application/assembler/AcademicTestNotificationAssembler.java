package com.kustacks.kuring.new_message.application.assembler;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.enums.TopicSuffixPolicy;
import com.kustacks.kuring.new_message.domain.event.AcademicTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;

import java.util.List;
import java.util.Map;

@UseCase
public class AcademicTestNotificationAssembler implements NotificationCommandAssembler<AcademicTestNotificationEvent> {

    @Override
    public boolean supports(MessageEvent event) {
        return event instanceof AcademicTestNotificationEvent;
    }

    @Override
    public List<NotificationCommand> assemble(AcademicTestNotificationEvent event) {
        String title = event.title();
        String body = event.body();
        String topic = TopicNames.ACADEMIC_EVENT_TOPIC;
        Map<String, String> data = Map.of();

        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic(topic, TopicSuffixPolicy.ALWAYS_ADD_DEV_SUFFIX),
                NotificationContent.of(title, body),
                MessageType.ACADEMIC,
                data
        );

        return List.of(command);
    }

}

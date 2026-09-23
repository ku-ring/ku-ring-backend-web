package com.kustacks.kuring.new_message.application.assembler;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.event.AcademicScheduleNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;

import java.util.List;
import java.util.Map;

@UseCase
public class AcademicScheduleNotificationAssembler implements NotificationCommandAssembler<AcademicScheduleNotificationEvent> {

    private static final String MESSAGE_DEFAULT = "오늘은 %s 일정이 있어요";

    @Override
    public Class<AcademicScheduleNotificationEvent> supportEventType() {
        return AcademicScheduleNotificationEvent.class;
    }

    @Override
    public List<NotificationCommand> assemble(AcademicScheduleNotificationEvent event) {
        String title = "[" + event.summary() + "]";
        String body = String.format(MESSAGE_DEFAULT, event.summary());
        String topic = TopicNames.ACADEMIC_EVENT_TOPIC;
        Map<String, String> data = Map.of();

        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic(topic),
                NotificationContent.of(title, body),
                MessageType.ACADEMIC,
                data
        );

        return List.of(command);
    }

}

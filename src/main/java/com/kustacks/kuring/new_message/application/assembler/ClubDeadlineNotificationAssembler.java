package com.kustacks.kuring.new_message.application.assembler;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.event.ClubDeadlineNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;

import java.util.List;
import java.util.Map;

@UseCase
public class ClubDeadlineNotificationAssembler implements NotificationCommandAssembler<ClubDeadlineNotificationEvent> {

    private static final String D_DAY_1_TITLE = "[D-1] %s 동아리 모집";
    private static final String D_DAY_1_BODY = "내일 마감되기 전에 지원하세요!";

    @Override
    public Class<ClubDeadlineNotificationEvent> supportEventType() {
        return ClubDeadlineNotificationEvent.class;
    }

    @Override
    public List<NotificationCommand> assemble(ClubDeadlineNotificationEvent event) {
        String title = String.format(D_DAY_1_TITLE, event.clubName());
        String body = D_DAY_1_BODY;
        String topic = TopicNames.clubTopic(event.clubId());
        Map<String, String> data = Map.of("clubId", String.valueOf(event.clubId()));

        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic(topic),
                NotificationContent.of(title, body),
                MessageType.CLUB,
                data
        );
        return List.of(command);
    }
}

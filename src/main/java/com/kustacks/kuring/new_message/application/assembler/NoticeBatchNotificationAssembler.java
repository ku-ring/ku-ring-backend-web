package com.kustacks.kuring.new_message.application.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.event.NoticeBatchNotificationEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@UseCase
@RequiredArgsConstructor
public class NoticeBatchNotificationAssembler implements NotificationCommandAssembler<NoticeBatchNotificationEvent> {

    private static final String NOTIFICATION_TITLE = "새로운 공지가 왔어요!";
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MessageEvent event) {
        return event instanceof NoticeBatchNotificationEvent;
    }

    @Override
    public List<NotificationCommand> assemble(NoticeBatchNotificationEvent event) {
        return event.notices().stream()
                .map(this::toCommand)
                .toList();
    }

    private NotificationCommand toCommand(NoticeBatchNotificationEvent.NoticeMessageDto dto) {
        String title = "[" + dto.categoryKorName() + "] " + NOTIFICATION_TITLE;
        String body = dto.subject();
        String topic = TopicNames.noticeTopic(dto.category());
        Map<String, String> data = objectMapper.convertValue(dto, Map.class);

        return new NotificationCommand(
                NotificationTarget.topic(topic),
                NotificationContent.of(title, body),
                MessageType.NOTICE,
                data
        );
    }
}

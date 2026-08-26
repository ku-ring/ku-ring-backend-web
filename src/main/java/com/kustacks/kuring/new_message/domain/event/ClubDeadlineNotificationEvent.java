package com.kustacks.kuring.new_message.domain.event;

public record ClubDeadlineNotificationEvent(
        Long clubId,
        String clubName
) implements MessageEvent {
}

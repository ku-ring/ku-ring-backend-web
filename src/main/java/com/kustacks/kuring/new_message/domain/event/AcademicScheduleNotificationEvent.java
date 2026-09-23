package com.kustacks.kuring.new_message.domain.event;

public record AcademicScheduleNotificationEvent(
        String summary
) implements MessageEvent {
}

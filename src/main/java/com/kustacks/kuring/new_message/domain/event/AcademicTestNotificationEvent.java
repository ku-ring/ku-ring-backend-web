package com.kustacks.kuring.new_message.domain.event;

public record AcademicTestNotificationEvent(
        String title,
        String body
) implements MessageEvent {
}

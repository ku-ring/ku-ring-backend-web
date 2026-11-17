package com.kustacks.kuring.new_message.domain.event;

public record AdminNotificationEvent(
        String title,
        String body,
        String url
) implements MessageEvent {
}

package com.kustacks.kuring.new_message.domain.event;

public record AlertSendEvent(
        String title,
        String content
) implements MessageEvent {
}

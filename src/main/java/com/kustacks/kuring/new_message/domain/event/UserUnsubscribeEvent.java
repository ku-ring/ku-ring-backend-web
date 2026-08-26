package com.kustacks.kuring.new_message.domain.event;

public record UserUnsubscribeEvent(
        String token,
        String topic
) {
}

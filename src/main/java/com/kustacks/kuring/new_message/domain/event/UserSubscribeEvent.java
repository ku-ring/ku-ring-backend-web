package com.kustacks.kuring.new_message.domain.event;

public record UserSubscribeEvent(
        String token,
        String topic
) {
}

package com.kustacks.kuring.message.application.port.in.dto;

public record UserSubscribeCommand(
        String token,
        String topic
) {
}

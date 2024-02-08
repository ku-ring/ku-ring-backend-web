package com.kustacks.kuring.message.application.port.in.dto;

public record UserUnsubscribeCommand(
        String token,
        String topic
) {
}

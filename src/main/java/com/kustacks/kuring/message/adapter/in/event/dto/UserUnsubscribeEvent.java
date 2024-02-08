package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.UserUnsubscribeCommand;

public record UserUnsubscribeEvent(
        String token,
        String topic
) {
    public UserUnsubscribeCommand toCommand() {
        return new UserUnsubscribeCommand(token, topic);
    }
}

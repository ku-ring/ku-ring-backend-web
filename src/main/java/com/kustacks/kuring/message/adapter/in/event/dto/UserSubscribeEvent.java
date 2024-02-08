package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;

public record UserSubscribeEvent(
        String token,
        String topic
) {
    public UserSubscribeCommand toCommand() {
        return new UserSubscribeCommand(token, topic);
    }
}

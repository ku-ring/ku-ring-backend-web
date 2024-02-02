package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.UserTokenValidationCommand;

public record UserTokenValidationEvent(
        String token
) {
    public UserTokenValidationCommand toCommand() {
        return new UserTokenValidationCommand(token);
    }
}

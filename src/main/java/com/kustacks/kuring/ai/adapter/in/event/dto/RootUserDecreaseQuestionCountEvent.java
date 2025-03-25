package com.kustacks.kuring.ai.adapter.in.event.dto;

import com.kustacks.kuring.user.application.port.in.dto.RootUserDecreaseQuestionCountCommand;

public record RootUserDecreaseQuestionCountEvent(
        String userId,
        String email
) {
    public RootUserDecreaseQuestionCountCommand toCommand() {
        return new RootUserDecreaseQuestionCountCommand(userId, email);
    }
}

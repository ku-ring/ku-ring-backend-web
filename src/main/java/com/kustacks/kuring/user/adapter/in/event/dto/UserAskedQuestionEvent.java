package com.kustacks.kuring.user.adapter.in.event.dto;

import com.kustacks.kuring.user.application.port.in.dto.UserDecreaseQuestionCountCommand;

public record UserAskedQuestionEvent(
        String userId,
        String email
) {

    public UserDecreaseQuestionCountCommand toCommand() {
        return new UserDecreaseQuestionCountCommand(userId, email);
    }
}

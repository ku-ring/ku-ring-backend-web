package com.kustacks.kuring.user.application.port.in.dto;

public record UserDecreaseQuestionCountCommand(
        String userId,
        String email
) {
}

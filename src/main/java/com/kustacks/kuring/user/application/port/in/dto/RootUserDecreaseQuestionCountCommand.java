package com.kustacks.kuring.user.application.port.in.dto;

public record RootUserDecreaseQuestionCountCommand(
        String userId,
        String email
) {
}

package com.kustacks.kuring.user.application.port.in.dto;

public record UserFeedbackCommand(
        String userToken,
        String content
) {
}

package com.kustacks.kuring.user.application.port.in.dto;

import java.time.LocalDateTime;

public record AdminFeedbacksResult(
        String contents,
        Long userId,
        LocalDateTime createdAt
) {
}

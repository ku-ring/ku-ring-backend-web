package com.kustacks.kuring.user.application.port.in.dto;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;

import java.time.LocalDateTime;

public record AdminFeedbacksResult(
        String contents,
        Long userId,
        LocalDateTime createdAt
) {
    public static AdminFeedbacksResult from(FeedbackDto dto) {
        return new AdminFeedbacksResult(
                dto.getContents(),
                dto.getUserId(),
                dto.getCreatedAt()
        );
    }
}

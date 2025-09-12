package com.kustacks.kuring.report.application.port.in.dto;

import java.time.LocalDateTime;

public record AdminReportsResult(
        Long id,
        Long targetId,
        Long reporterId,
        String type,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AdminReportsResult of(
            Long id,
            Long targetId,
            Long reporterId,
            String type,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new AdminReportsResult(id, targetId, reporterId, type, content, createdAt, updatedAt);
    }
}

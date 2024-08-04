package com.kustacks.kuring.admin.adapter.in.web.dto;

import com.kustacks.kuring.alert.domain.AlertStatus;

import java.time.LocalDateTime;

public record AdminAlertResponse(
        Long id,
        String title,
        String content,
        AlertStatus status,
        LocalDateTime wakeTime
) {
    public static AdminAlertResponse of(
            Long id, String title, String content,
            AlertStatus status, LocalDateTime alertTime
    ) {
        return new AdminAlertResponse(id, title, content, status, alertTime);
    }
};

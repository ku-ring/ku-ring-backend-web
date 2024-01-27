package com.kustacks.kuring.admin.application.port.in.dto;

public record TestNotificationCommand(
        String category,
        String subject,
        String articleId
) {
}

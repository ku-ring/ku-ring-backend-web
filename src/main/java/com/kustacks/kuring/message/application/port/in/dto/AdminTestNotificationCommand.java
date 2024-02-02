package com.kustacks.kuring.message.application.port.in.dto;

public record AdminTestNotificationCommand(
        String articleId,
        String postedDate,
        String categoryName,
        String subject,
        String korName,
        String url
) {
}

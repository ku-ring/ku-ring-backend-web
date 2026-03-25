package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.AdminTestNotificationCommand;
import lombok.Builder;

@Builder
public record AdminTestNotificationEvent(
         String noticeId,
         String type,
         String articleId,
         String postedDate,
         String subject,
         String category,
         String categoryKorName,
         String baseUrl
) {
    public AdminTestNotificationCommand toCommand() {
        return new AdminTestNotificationCommand(
                noticeId,
                articleId,
                postedDate,
                category,
                subject,
                categoryKorName,
                baseUrl
        );
    }
}

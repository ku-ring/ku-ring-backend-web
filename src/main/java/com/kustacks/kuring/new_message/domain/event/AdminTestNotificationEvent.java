package com.kustacks.kuring.new_message.domain.event;

import lombok.Builder;

@Builder
public record AdminTestNotificationEvent(
        String articleId,
        String postedDate,
        String category,
        String subject,
        String categoryKorName,
        String baseUrl
) implements MessageEvent {
}

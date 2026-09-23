package com.kustacks.kuring.new_message.domain.event;

import java.util.List;

public record NoticeBatchNotificationEvent(
        List<NoticeMessageDto> notices
) implements MessageEvent {

    public record NoticeMessageDto(
            String articleId,
            String postedDate,
            String subject,
            String category,
            String categoryKorName,
            String baseUrl
    ) {}

}

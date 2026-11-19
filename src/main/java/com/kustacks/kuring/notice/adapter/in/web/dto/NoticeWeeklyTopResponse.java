package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.in.dto.NoticeWeeklyTopResult;

public record NoticeWeeklyTopResponse(
        Long id,
        String articleId,
        String subject,
        String category,
        String categoryKorName,
        Integer viewCount,
        String postedDate,
        String url
) {
    public static NoticeWeeklyTopResponse from(NoticeWeeklyTopResult result) {
        return new NoticeWeeklyTopResponse(
                result.id(),
                result.articleId(),
                result.subject(),
                result.category(),
                result.categoryKorName(),
                result.viewCount(),
                result.postedDate(),
                result.url()
        );
    }
}

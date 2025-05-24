package com.kustacks.kuring.notice.application.port.in.dto;

public record NoticeContentSearchResult(
        Long id,
        String articleId,
        String postedDate,
        String subject,
        String category,
        String baseUrl,
        Long commentCount
) {
}

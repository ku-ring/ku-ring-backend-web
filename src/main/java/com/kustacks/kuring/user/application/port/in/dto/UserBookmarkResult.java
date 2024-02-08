package com.kustacks.kuring.user.application.port.in.dto;

public record UserBookmarkResult(
        String articleId,
        String postedDate,
        String subject,
        String category,
        String baseUrl
) {
}

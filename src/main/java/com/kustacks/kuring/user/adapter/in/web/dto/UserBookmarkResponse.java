package com.kustacks.kuring.user.adapter.in.web.dto;

import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkResult;

public record UserBookmarkResponse(
        String articleId,
        String postedDate,
        String subject,
        String category,
        String baseUrl
) {

    public static UserBookmarkResponse from(UserBookmarkResult result) {
        return new UserBookmarkResponse(
                result.articleId(),
                result.postedDate(),
                result.subject(),
                result.category(),
                result.baseUrl()
        );
    }
}

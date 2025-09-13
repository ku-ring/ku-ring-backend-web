package com.kustacks.kuring.notice.application.port.in.dto;

public record NoticeRangeLookupResult(
        Long id,
        String articleId,
        String postedDate,
        String url,
        String subject,
        String category,
        Boolean important,
        Boolean graduated,
        Long commentCount
) {
}

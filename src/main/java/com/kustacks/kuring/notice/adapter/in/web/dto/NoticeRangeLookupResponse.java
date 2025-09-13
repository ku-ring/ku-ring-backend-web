package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.in.dto.NoticeRangeLookupResult;

public record NoticeRangeLookupResponse(
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
    public static NoticeRangeLookupResponse from(NoticeRangeLookupResult result) {
        return new NoticeRangeLookupResponse(
                result.id(),
                result.articleId(),
                result.postedDate(),
                result.url(),
                result.subject(),
                result.category(),
                result.important(),
                result.graduated(),
                result.commentCount()
        );
    }
}

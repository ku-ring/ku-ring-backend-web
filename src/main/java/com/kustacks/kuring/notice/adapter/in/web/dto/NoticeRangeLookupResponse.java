package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.in.dto.NoticeRangeLookupResult;

public record NoticeRangeLookupResponse(
        String articleId,
        String postedDate,
        String url,
        String subject,
        String category,
        Boolean important
){
    public static NoticeRangeLookupResponse from(NoticeRangeLookupResult result) {
        return new NoticeRangeLookupResponse(
                result.articleId(),
                result.postedDate(),
                result.url(),
                result.subject(),
                result.category(),
                result.important()
        );
    }
}

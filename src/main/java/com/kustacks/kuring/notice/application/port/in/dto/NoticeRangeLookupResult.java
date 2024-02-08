package com.kustacks.kuring.notice.application.port.in.dto;

public record NoticeRangeLookupResult(
        String articleId,
        String postedDate,
        String url,
        String subject,
        String category,
        Boolean important
){
}

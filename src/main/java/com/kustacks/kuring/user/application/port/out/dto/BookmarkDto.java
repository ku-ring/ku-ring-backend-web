package com.kustacks.kuring.user.application.port.out.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkDto {

    private String articleId;

    private String postedDate;

    private String subject;

    private String category;

    private String baseUrl;

    @QueryProjection
    public BookmarkDto(String articleId, String postedDate, String subject, String category, String baseUrl) {
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.category = category;
        this.baseUrl = baseUrl;
    }
}

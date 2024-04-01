package com.kustacks.kuring.user.application.port.out.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkDto {

    private static final String SPACE = " ";
    private static final int DATE_INDEX = 0;

    private String articleId;

    private String postedDate;

    private String subject;

    private String category;

    private String baseUrl;

    @QueryProjection
    public BookmarkDto(String articleId, String postedDate, String subject, String category, String baseUrl) {
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(category, "category must not be null");
        Assert.notNull(baseUrl, "baseUrl must not be null");

        this.articleId = articleId;
        this.postedDate = postedDate.split(SPACE)[DATE_INDEX];
        this.subject = subject;
        this.category = category;
        this.baseUrl = baseUrl;
    }
}

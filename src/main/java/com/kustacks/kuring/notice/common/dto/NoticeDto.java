package com.kustacks.kuring.notice.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDto {

    private String articleId;

    private String postedDate;

    private String subject;

    private String category;

    @Builder
    @QueryProjection
    public NoticeDto(String articleId, String postedDate, String subject, String category) {
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(category, "category must not be null");

        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.category = category;
    }
}

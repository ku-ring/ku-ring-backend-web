package com.kustacks.kuring.notice.application.port.out.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDto {

    private static final String SPACE = " ";
    private static final int DATE_INDEX = 0;

    private Long id;

    private String articleId;

    private String postedDate;

    private String url;

    private String subject;

    private String category;

    private Boolean important;

    private Boolean graduated;

    private Long commentCount;

    @QueryProjection
    public NoticeDto(
            Long id, String articleId, String postedDate, String url, String subject,
            String category, Boolean important, Boolean graduated, Long commentCount
    ) {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(url, "url must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(category, "category must not be null");
        Assert.notNull(important, "important must not be null");

        this.id = id;
        this.articleId = articleId;
        this.postedDate = postedDate.split(SPACE)[DATE_INDEX];
        this.url = url;
        this.subject = subject;
        this.category = category;
        this.important = important;
        this.graduated = graduated;
        this.commentCount = commentCount;
    }
}

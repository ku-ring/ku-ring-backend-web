package com.kustacks.kuring.notice.application.port.out.dto;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeViewCountDto {

    private Long id;

    private String articleId;

    private String subject;

    private CategoryName categoryName;

    private Integer viewCount;

    private LocalDateTime postedDate;

    private String url;

    @QueryProjection
    public NoticeViewCountDto(
            Long id, String articleId, String subject, CategoryName categoryName,
            Integer viewCount, LocalDateTime postedDate, String url
    ) {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(categoryName, "categoryName must not be null");
        Assert.notNull(viewCount, "viewCount must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(url, "url must not be null");

        this.id = id;
        this.articleId = articleId;
        this.subject = subject;
        this.categoryName = categoryName;
        this.viewCount = viewCount;
        this.postedDate = postedDate;
        this.url = url;
    }
}

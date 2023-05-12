package com.kustacks.kuring.search.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class NoticeSearchDto {

    private String articleId;

    private String postedDate;

    private String subject;

    @JsonProperty("category")
    private String categoryName;

    private String baseUrl;

    @QueryProjection
    public NoticeSearchDto(String articleId, String postedDate, String subject, String categoryName) {
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.categoryName = categoryName;
    }

    @Builder
    private NoticeSearchDto(String articleId, String postedDate, String subject, String categoryName, String baseUrl) {
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(categoryName, "categoryName must not be null");
        Assert.notNull(baseUrl, "baseUrl must not be null");

        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.categoryName = categoryName;
        this.baseUrl = baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

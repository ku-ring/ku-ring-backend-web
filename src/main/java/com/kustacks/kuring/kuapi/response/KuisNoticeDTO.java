package com.kustacks.kuring.kuapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.kuapi.NoticeCategory;
import lombok.Getter;

@Getter
public class KuisNoticeDTO {
    @JsonProperty("ARTICLE_ID")
    private String articleId;

    @JsonProperty("POSTED_DT")
    private String postedDate;

    @JsonProperty("SUBJECT")
    private String subject;

    public Notice toEntity(Category category) {
        return Notice.builder()
                .articleId(this.articleId)
                .postedDate(this.postedDate)
                .subject(this.subject)
                .category(category)
                .build();
    }
}

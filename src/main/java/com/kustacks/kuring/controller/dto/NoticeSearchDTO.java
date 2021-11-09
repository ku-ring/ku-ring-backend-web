package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustacks.kuring.domain.notice.Notice;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeSearchDTO {

    @JsonProperty("articleId")
    private String articleId;

    @JsonProperty("postedDate")
    private String postedDate;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("category")
    private String categoryName;

    @JsonProperty("baseUrl")
    private String baseUrl;

    @Builder
    public NoticeSearchDTO(String articleId, String postedDate, String subject, String categoryName, String baseUrl) {
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.categoryName = categoryName;
        this.baseUrl = baseUrl;
    }

    public static NoticeSearchDTO entityToDTO(Notice notice, String baseUrl) {
        return NoticeSearchDTO.builder()
                .articleId(notice.getArticleId())
                .postedDate(notice.getPostedDate())
                .subject(notice.getSubject())
                .categoryName(notice.getCategory().getName())
                .baseUrl(baseUrl)
                .build();
    }
}

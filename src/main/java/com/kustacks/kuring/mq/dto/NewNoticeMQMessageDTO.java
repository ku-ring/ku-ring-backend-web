package com.kustacks.kuring.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NewNoticeMQMessageDTO extends MQMessageDTO {

    @JsonProperty("articleId")
    private String articleId;

    @JsonProperty("postedDate")
    private String postedDate;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("category")
    private String category;

    @JsonProperty("baseUrl")
    private String baseUrl;

    @JsonProperty("fullUrl")
    private String fullUrl;

    @Builder
    public NewNoticeMQMessageDTO(String articleId, String postedDate, String subject, String category, String baseUrl, String fullUrl) {
        this.type = "notice";
        this.token = null;
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.category = category;
        this.baseUrl = baseUrl;
        this.fullUrl = fullUrl;
    }
}

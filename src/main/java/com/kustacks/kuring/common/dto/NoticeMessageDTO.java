package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class NoticeMessageDTO extends FCMMessageDTO {

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

    public NoticeMessageDTO(String articleId, String postedDate, String subject, String category, String baseUrl) {
        this.type = "notice";
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.category = category;
        this.baseUrl = baseUrl;
    }
}

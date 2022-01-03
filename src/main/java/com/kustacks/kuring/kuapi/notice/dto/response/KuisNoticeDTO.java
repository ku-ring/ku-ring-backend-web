package com.kustacks.kuring.kuapi.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KuisNoticeDTO {
    @JsonProperty("ARTICLE_ID")
    private String articleId;

    @JsonProperty("POSTED_DT")
    private String postedDate;

    @JsonProperty("SUBJECT")
    private String subject;
}

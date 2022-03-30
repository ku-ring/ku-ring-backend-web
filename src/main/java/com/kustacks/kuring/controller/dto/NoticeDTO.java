package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NoticeDTO {
    @JsonProperty("articleId")
    private String articleId;

    @JsonProperty("postedDate")
    private String postedDate;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("category")
    private String categoryName;

    @JsonProperty("fullUrl")
    private String fullUrl;
}

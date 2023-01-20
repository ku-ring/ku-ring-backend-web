package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveFeedbackRequestDTO {

    @JsonProperty("id")
    private String token;

    @JsonProperty("content")
    private String content;

    @Builder
    public SaveFeedbackRequestDTO(String token, String content) {
        this.token = token;
        this.content = content;
    }
}

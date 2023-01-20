package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class AdminMessageDTO extends FCMMessageDTO {

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    public AdminMessageDTO(String title, String body) {
        this.type = "admin";
        this.title = title;
        this.body = body;
    }
}

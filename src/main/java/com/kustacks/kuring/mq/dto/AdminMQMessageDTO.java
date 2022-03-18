package com.kustacks.kuring.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminMQMessageDTO extends MQMessageDTO {

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    @Builder
    public AdminMQMessageDTO(String title, String body) {
        this.type = "admin";
        this.token = null;
        this.title = title;
        this.body = body;
    }
}

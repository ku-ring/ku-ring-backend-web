package com.kustacks.kuring.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateRequestMQMessageDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("category")
    private String category;
}

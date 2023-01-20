package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HeartBeatResponseDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("content")
    private String content;
}

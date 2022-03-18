package com.kustacks.kuring.mq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MQMessageDTO {

    @JsonProperty("type")
    protected String type;

    @JsonProperty("token")
    protected String token;
}

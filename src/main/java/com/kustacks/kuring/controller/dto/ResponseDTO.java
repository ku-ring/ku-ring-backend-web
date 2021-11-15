package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

    @JsonProperty("isSuccess")
    private boolean success;

    @JsonProperty("resultMsg")
    private String resultMsg;

    @JsonProperty("resultCode")
    private int resultCode;
}

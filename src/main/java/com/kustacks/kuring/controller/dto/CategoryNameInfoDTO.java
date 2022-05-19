package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public class CategoryNameInfoDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("korName")
    private String korName;

    @JsonProperty("shortName")
    private String shortName;

    @Builder
    public CategoryNameInfoDTO(String name, String korName, String shortName) {
        this.name = name;
        this.korName = korName;
        this.shortName = shortName;
    }
}

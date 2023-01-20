package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryDTO {

    @JsonProperty("name")
    private String name;

    @Builder
    public CategoryDTO(String name) {
        this.name = name;
    }
}

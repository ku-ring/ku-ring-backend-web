package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class SubscribeCategoriesRequestDTO {

    @JsonProperty("id")
    private String token;

    @JsonProperty("categories")
    private List<String> categories;

    @Builder
    public SubscribeCategoriesRequestDTO(String token, List<String> categories) {
        this.categories = categories;
        this.token = token;
    }
}

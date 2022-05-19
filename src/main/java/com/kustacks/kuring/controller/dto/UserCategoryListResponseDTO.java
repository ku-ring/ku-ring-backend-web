package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UserCategoryListResponseDTO extends ResponseDTO {

    @JsonProperty("categories")
    private List<String> categories;

    @Builder
    public UserCategoryListResponseDTO(List<String> categories) {
        super(true, "성공", HttpStatus.OK.value());
        this.categories = categories;
    }
}

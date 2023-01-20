package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CategoryListResponseDTO extends ResponseDTO {

    @JsonProperty("categories")
    private List<String> categories;

    @Builder
    public CategoryListResponseDTO(List<String> categories) {
        super(true, "성공", 200);
        this.categories = categories;
    }
}

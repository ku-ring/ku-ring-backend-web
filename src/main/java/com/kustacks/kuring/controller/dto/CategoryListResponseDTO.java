package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CategoryListResponseDTO extends ResponseDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("categories")
    private List<CategoryHierarchyDTO> categories;

    @Builder
    public CategoryListResponseDTO(String type, List<CategoryHierarchyDTO> categories) {
        super(true, "성공", 200);
        this.type = type;
        this.categories = categories;
    }
}

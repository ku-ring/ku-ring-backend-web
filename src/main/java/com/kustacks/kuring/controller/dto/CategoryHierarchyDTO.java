package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryHierarchyDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("categories")
    private List<CategoryHierarchyDTO> categories;

    @JsonProperty("name")
    private String name;

    @JsonProperty("korName")
    private String korName;

    @JsonProperty("shortName")
    private String shortName;

    // 중간 계층 카테고리가 사용하는 생성자
    public CategoryHierarchyDTO(String type, List<CategoryHierarchyDTO> categories) {
        this.type = type;
        this.categories = categories;
    }

    // leaf 계층(실제 구독 가능한) 카테고리가 사용하는 생성자
    public CategoryHierarchyDTO(String name, String korName, String shortName) {
        this.name = name;
        this.korName = korName;
        this.shortName = shortName;
    }
}

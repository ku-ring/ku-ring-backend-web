package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CategoryDto;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;

import java.util.List;

public record CategoryListResponse(
        List<CategoryDto> categories
) {

    public static CategoryListResponse from(List<CategoryResult> results) {
        return new CategoryListResponse(results.stream()
                .map(CategoryDto::from)
                .toList());
    }
}

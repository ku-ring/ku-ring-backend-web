package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CategoryDto;

import java.util.List;

public record CategoryListResponse(
        List<CategoryDto> categories
) {
}

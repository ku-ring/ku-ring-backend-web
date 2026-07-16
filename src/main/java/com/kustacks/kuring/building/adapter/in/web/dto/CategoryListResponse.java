package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CategoryItem;

import java.util.List;

public record CategoryListResponse(
        List<CategoryItem> categories
) {
}

package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;

public record CategoryDto(
        String name,
        String korName,
        Integer displayOrder
) {

    public static CategoryDto from(CategoryResult result) {
        return new CategoryDto(
                result.name(),
                result.korName(),
                result.displayOrder()
        );
    }
}

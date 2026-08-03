package com.kustacks.kuring.building.application.port.out.dto;

public record CampusPlaceCategoryReadModel(
        String code,
        String korName,
        int displayOrder
) {
}

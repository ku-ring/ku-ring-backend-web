package com.kustacks.kuring.building.adapter.in.web.dto.model;

public record CampusPlaceDetail(
        Long id,
        String name,
        String category,
        String categoryKorName,
        String imageUrl,
        String locationType,
        String floor,
        String locationDetail,
        Integer quantity,
        CurrentOperatingHours currentOperatingHours,
        String externalUrl
) {
}

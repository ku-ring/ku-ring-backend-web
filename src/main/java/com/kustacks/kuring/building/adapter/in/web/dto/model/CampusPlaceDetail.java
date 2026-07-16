package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.domain.CampusPlaceLocationType;

public record CampusPlaceDetail(
        Long id,
        String name,
        String category,
        String categoryKorName,
        String imageUrl,
        CampusPlaceLocationType locationType,
        String floor,
        String locationDetail,
        Integer quantity,
        CurrentOperatingHours currentOperatingHours,
        String externalUrl
) {
}

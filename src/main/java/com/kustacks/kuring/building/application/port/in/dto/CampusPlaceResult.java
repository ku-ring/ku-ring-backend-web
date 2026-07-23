package com.kustacks.kuring.building.application.port.in.dto;

import com.kustacks.kuring.building.domain.CampusPlaceLocationType;

public record CampusPlaceResult(
        Long id,
        String name,
        String category,
        String categoryKorName,
        String imageUrl,
        CampusPlaceLocationType locationType,
        String floor,
        String locationDetail,
        Integer quantity,
        CurrentOperatingHoursResult currentOperatingHours,
        String externalUrl,
        BuildingSummaryResult building
) {
}

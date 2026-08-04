package com.kustacks.kuring.building.application.port.in.dto;

import com.kustacks.kuring.building.domain.CampusPlaceLocationType;

import java.util.List;

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
        List<OperatingHoursResult> operatingHours,
        String externalUrl,
        BuildingSummaryResult building
) {
}

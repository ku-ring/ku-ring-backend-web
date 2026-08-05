package com.kustacks.kuring.building.application.port.out.dto;

import com.kustacks.kuring.building.domain.CampusPlaceLocationType;

import java.util.List;

public record CampusPlaceReadModel(
        Long id,
        String name,
        String categoryCode,
        String categoryKorName,
        String imagePath,
        CampusPlaceLocationType locationType,
        String floor,
        String locationDetail,
        Integer quantity,
        List<OperatingHoursReadModel> operatingHours,
        String externalUrl,
        BuildingSummaryReadModel building
) {
}

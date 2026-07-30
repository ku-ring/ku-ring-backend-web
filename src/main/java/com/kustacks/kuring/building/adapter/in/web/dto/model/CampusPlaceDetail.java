package com.kustacks.kuring.building.adapter.in.web.dto.model;

import java.util.List;

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
        List<OperatingHoursDto> operatingHours,
        String externalUrl
) {
}

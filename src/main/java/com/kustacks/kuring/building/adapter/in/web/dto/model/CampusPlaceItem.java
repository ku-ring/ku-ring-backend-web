package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;

public record CampusPlaceItem(
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
        String externalUrl,
        BuildingSummary building
) {

    public static CampusPlaceItem from(CampusPlaceResult result) {
        return new CampusPlaceItem(
                result.id(),
                result.name(),
                result.category(),
                result.categoryKorName(),
                result.imageUrl(),
                result.locationType().name(),
                result.floor(),
                result.locationDetail(),
                result.quantity(),
                CurrentOperatingHours.from(result.currentOperatingHours()),
                result.externalUrl(),
                BuildingSummary.from(result.building())
        );
    }
}

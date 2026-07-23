package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;

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

    public static CampusPlaceDetail from(CampusPlaceResult result) {
        return new CampusPlaceDetail(
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
                result.externalUrl()
        );
    }
}

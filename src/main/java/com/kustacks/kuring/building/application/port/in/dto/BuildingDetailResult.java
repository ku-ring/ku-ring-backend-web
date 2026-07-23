package com.kustacks.kuring.building.application.port.in.dto;

import java.util.List;

public record BuildingDetailResult(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String imageUrl,
        CurrentOperatingHoursResult currentOperatingHours,
        List<CampusPlaceResult> campusPlaces
) {
}

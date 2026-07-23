package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceDetail;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CurrentOperatingHours;
import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;

import java.util.List;

public record BuildingDetailResponse(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String imageUrl,
        CurrentOperatingHours currentOperatingHours,
        List<CampusPlaceDetail> campusPlaces
) {

    public static BuildingDetailResponse from(BuildingDetailResult result) {
        return new BuildingDetailResponse(
                result.id(),
                result.name(),
                result.address(),
                result.latitude(),
                result.longitude(),
                result.imageUrl(),
                CurrentOperatingHours.from(result.currentOperatingHours()),
                result.campusPlaces().stream()
                        .map(CampusPlaceDetail::from)
                        .toList()
        );
    }
}

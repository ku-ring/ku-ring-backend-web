package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceDetail;
import com.kustacks.kuring.building.adapter.in.web.dto.model.OperatingHoursDto;

import java.util.List;

public record BuildingDetailResponse(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String imageUrl,
        List<OperatingHoursDto> operatingHours,
        List<CampusPlaceDetail> campusPlaces
) {
}

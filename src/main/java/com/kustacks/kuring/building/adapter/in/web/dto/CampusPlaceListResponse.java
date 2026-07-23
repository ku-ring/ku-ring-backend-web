package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceItem;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;

import java.util.List;

public record CampusPlaceListResponse(
        List<CampusPlaceItem> campusPlaces
) {

    public static CampusPlaceListResponse from(List<CampusPlaceResult> results) {
        return new CampusPlaceListResponse(results.stream()
                .map(CampusPlaceItem::from)
                .toList());
    }
}

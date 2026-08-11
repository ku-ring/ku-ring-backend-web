package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingSummary;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceItem;
import com.kustacks.kuring.building.application.port.in.dto.CampusMapSearchResult;

import java.util.List;

public record CampusMapSearchResponse(
        List<BuildingSummary> buildings,
        List<CampusPlaceItem> campusPlaces
) {

    public static CampusMapSearchResponse from(CampusMapSearchResult result) {
        return new CampusMapSearchResponse(
                result.buildings().stream()
                        .map(BuildingSummary::from)
                        .toList(),
                result.campusPlaces().stream()
                        .map(CampusPlaceItem::from)
                        .toList()
        );
    }
}

package com.kustacks.kuring.building.application.port.in.dto;

import java.util.List;

public record CampusMapSearchResult(
        List<BuildingSummaryResult> buildings,
        List<CampusPlaceResult> campusPlaces
) {
}

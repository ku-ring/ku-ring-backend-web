package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceItem;

import java.util.List;

public record CampusPlaceListResponse(
        List<CampusPlaceItem> campusPlaces
) {
}

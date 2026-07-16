package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingSummary;

import java.util.List;

public record BuildingSearchResponse(
        List<BuildingSummary> buildings
) {
}

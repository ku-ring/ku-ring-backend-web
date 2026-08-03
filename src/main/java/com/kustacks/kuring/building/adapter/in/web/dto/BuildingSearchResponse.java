package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingSummary;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;

import java.util.List;

public record BuildingSearchResponse(
        List<BuildingSummary> buildings
) {

    public static BuildingSearchResponse from(List<BuildingSummaryResult> results) {
        return new BuildingSearchResponse(results.stream()
                .map(BuildingSummary::from)
                .toList());
    }
}

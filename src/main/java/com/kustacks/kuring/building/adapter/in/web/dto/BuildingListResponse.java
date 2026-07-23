package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingSummary;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;

import java.util.List;

public record BuildingListResponse(
        List<BuildingSummary> buildings
) {

    public static BuildingListResponse from(List<BuildingSummaryResult> results) {
        return new BuildingListResponse(results.stream()
                .map(BuildingSummary::from)
                .toList());
    }
}

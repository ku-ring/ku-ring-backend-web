package com.kustacks.kuring.building.adapter.in.web.dto;

import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingOverview;
import com.kustacks.kuring.building.application.port.in.dto.BuildingOverviewResult;

import java.util.List;

public record BuildingListResponse(
        List<BuildingOverview> buildings
) {

    public static BuildingListResponse from(List<BuildingOverviewResult> results) {
        return new BuildingListResponse(results.stream()
                .map(BuildingOverview::from)
                .toList());
    }
}

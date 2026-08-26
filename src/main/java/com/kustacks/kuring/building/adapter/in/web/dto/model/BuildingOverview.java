package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.application.port.in.dto.BuildingOverviewResult;

public record BuildingOverview(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        Integer displayOrder
) {

    public static BuildingOverview from(BuildingOverviewResult result) {
        return new BuildingOverview(
                result.id(),
                result.name(),
                result.address(),
                result.latitude(),
                result.longitude(),
                result.displayOrder()
        );
    }
}

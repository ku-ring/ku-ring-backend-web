package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;

public record BuildingSummary(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude
) {

    public static BuildingSummary from(BuildingSummaryResult result) {
        return new BuildingSummary(
                result.id(),
                result.name(),
                result.address(),
                result.latitude(),
                result.longitude()
        );
    }
}

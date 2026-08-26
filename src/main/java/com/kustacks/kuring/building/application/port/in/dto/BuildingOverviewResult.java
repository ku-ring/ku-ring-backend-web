package com.kustacks.kuring.building.application.port.in.dto;

public record BuildingOverviewResult(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        Integer displayOrder
) {
}

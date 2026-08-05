package com.kustacks.kuring.building.application.port.out.dto;

import java.util.List;

public record BuildingDetailReadModel(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String imagePath,
        List<OperatingHoursReadModel> operatingHours
) {
}

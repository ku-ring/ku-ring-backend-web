package com.kustacks.kuring.building.application.port.out.dto;

public record BuildingSummaryReadModel(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude
) {
}

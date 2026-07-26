package com.kustacks.kuring.building.application.port.in.dto;

public record BuildingSummaryResult(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude
) {
}

package com.kustacks.kuring.building.adapter.in.web.dto.model;

public record BuildingSummary(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude
) {
}

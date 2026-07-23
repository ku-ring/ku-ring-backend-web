package com.kustacks.kuring.building.adapter.in.web.dto.model;

public record CurrentOperatingHours(
        String period,
        String dayGroup,
        String status,
        String opensAt,
        String closesAt
) {
}

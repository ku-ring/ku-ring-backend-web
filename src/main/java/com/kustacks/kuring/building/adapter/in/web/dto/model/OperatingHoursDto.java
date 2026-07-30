package com.kustacks.kuring.building.adapter.in.web.dto.model;

public record OperatingHoursDto(
        String period,
        String dayGroup,
        String status,
        String opensAt,
        String closesAt,
        boolean isCurrent
) {
}

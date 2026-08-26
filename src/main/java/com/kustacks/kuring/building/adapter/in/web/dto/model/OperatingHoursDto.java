package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.application.port.in.dto.OperatingHoursResult;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public record OperatingHoursDto(
        String period,
        String dayGroup,
        String status,
        String opensAt,
        String closesAt,
        boolean isCurrent
) {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static OperatingHoursDto from(OperatingHoursResult result) {
        return new OperatingHoursDto(
                result.period().name(),
                result.dayGroup().name(),
                result.status().name(),
                format(result.opensAt()),
                format(result.closesAt()),
                result.isCurrent()
        );
    }

    private static String format(LocalTime time) {
        return time == null ? null : time.format(TIME_FORMATTER);
    }
}

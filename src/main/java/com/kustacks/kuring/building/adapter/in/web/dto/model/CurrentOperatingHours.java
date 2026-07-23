package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.application.port.in.dto.CurrentOperatingHoursResult;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public record CurrentOperatingHours(
        String period,
        String dayGroup,
        String status,
        String opensAt,
        String closesAt
) {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static CurrentOperatingHours from(CurrentOperatingHoursResult result) {
        return new CurrentOperatingHours(
                result.period().name(),
                result.dayGroup().name(),
                result.status().name(),
                format(result.opensAt()),
                format(result.closesAt())
        );
    }

    private static String format(LocalTime time) {
        return time == null ? null : time.format(TIME_FORMATTER);
    }
}

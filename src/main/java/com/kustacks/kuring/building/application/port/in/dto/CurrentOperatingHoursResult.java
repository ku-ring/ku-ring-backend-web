package com.kustacks.kuring.building.application.port.in.dto;

import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;

import java.time.LocalTime;

public record CurrentOperatingHoursResult(
        OperatingPeriod period,
        OperatingDayGroup dayGroup,
        OperatingHoursStatus status,
        LocalTime opensAt,
        LocalTime closesAt
) {
}

package com.kustacks.kuring.building.application.port.out.dto;

import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;

import java.time.LocalTime;

public record OperatingHoursReadModel(
        OperatingPeriod period,
        OperatingDayGroup dayGroup,
        OperatingHoursStatus status,
        LocalTime opensAt,
        LocalTime closesAt
) {

    public boolean matches(OperatingPeriod period, OperatingDayGroup dayGroup) {
        return this.period == period && this.dayGroup == dayGroup;
    }
}

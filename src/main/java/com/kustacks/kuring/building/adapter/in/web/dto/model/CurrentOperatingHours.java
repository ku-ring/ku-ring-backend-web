package com.kustacks.kuring.building.adapter.in.web.dto.model;

import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;

public record CurrentOperatingHours(
        OperatingPeriod period,
        OperatingDayGroup dayGroup,
        OperatingHoursStatus status,
        String opensAt,
        String closesAt
) {
}

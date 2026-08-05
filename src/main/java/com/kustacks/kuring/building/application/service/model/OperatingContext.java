package com.kustacks.kuring.building.application.service.model;

import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingPeriod;

public record OperatingContext(
        OperatingPeriod period,
        OperatingDayGroup dayGroup
) {
}

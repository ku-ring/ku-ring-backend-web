package com.kustacks.kuring.building.application.port.out;

import com.kustacks.kuring.building.domain.OperatingPeriod;

import java.time.LocalDate;

public interface AcademicPeriodPort {

    OperatingPeriod resolve(LocalDate date);
}

package com.kustacks.kuring.calendar.application.port.in.dto;

import java.time.LocalDate;

public record AcademicEventLookupCommand(
        LocalDate startDate,
        LocalDate endDate
) {
}

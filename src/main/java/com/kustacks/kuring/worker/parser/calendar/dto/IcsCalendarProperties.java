package com.kustacks.kuring.worker.parser.calendar.dto;

public record IcsCalendarProperties(
        String method,
        String prodId,
        String version,
        String xWrCalName
) {
}

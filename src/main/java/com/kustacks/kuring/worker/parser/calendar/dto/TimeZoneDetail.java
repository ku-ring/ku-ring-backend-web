package com.kustacks.kuring.worker.parser.calendar.dto;

public record TimeZoneDetail(
        String dtStart,
        String tzOffsetFrom,
        String tzOffsetTo
) {
}

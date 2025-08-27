package com.kustacks.kuring.worker.parser.calendar.dto;

public record IcsTimeZone(
        String tzid,
        TimeZoneDetail standard,
        TimeZoneDetail daylight
) {
}

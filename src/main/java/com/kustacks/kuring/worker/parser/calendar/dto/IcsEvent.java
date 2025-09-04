package com.kustacks.kuring.worker.parser.calendar.dto;

import lombok.Builder;

@Builder
public record IcsEvent(
        String uid,
        String summary,
        String description,
        String dtstart,
        String dtend,
        String classType,
        String priority,
        String dtstamp,
        String transp,
        String status,
        String sequence,
        String location
) {
}

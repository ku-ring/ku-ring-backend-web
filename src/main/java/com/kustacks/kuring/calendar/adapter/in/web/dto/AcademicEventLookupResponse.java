package com.kustacks.kuring.calendar.adapter.in.web.dto;

import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventResult;

import java.time.LocalDateTime;

public record AcademicEventLookupResponse(
        Long id,
        String eventUid,
        String summary,
        String description,
        String category,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static AcademicEventLookupResponse from(AcademicEventResult result) {
        return new AcademicEventLookupResponse(
                result.id(),
                result.eventUid(),
                result.summary(),
                result.description(),
                result.category(),
                result.startTime(),
                result.endTime()
        );
    }
}
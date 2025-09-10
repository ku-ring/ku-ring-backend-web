package com.kustacks.kuring.calendar.application.port.in.dto;

import java.time.LocalDateTime;

public record AcademicEventResult(
        Long id,
        String eventUid,
        String summary,
        String description,
        String category,
        String transparent,
        Integer sequence,
        Boolean notifyEnabled,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
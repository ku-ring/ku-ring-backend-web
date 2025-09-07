package com.kustacks.kuring.worker.update.calendar.dto;

import com.kustacks.kuring.calendar.domain.AcademicEvent;

import java.util.List;

public record EventUpdateResult(
        List<AcademicEvent> newEvents,
        List<AcademicEvent> updatedEvents
) {
}
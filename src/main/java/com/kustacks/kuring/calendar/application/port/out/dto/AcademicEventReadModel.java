package com.kustacks.kuring.calendar.application.port.out.dto;

import com.kustacks.kuring.calendar.domain.Transparent;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record AcademicEventReadModel(
        Long id,
        String eventUid,
        String summary,
        String description,
        String category,
        Transparent transparent,
        Integer sequence,
        Boolean notifyEnabled,
        LocalDateTime startTime,
        LocalDateTime endTime
) {

    @QueryProjection
    public AcademicEventReadModel(Long id, String eventUid, String summary, String description, String category, Transparent transparent, Integer sequence, Boolean notifyEnabled, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.eventUid = eventUid;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.transparent = transparent;
        this.sequence = sequence;
        this.notifyEnabled = notifyEnabled;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

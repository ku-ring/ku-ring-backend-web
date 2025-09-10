package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.domain.AcademicEvent;

import java.time.LocalDate;
import java.util.List;

interface AcademicEventQueryRepository {

    List<AcademicEvent> findByEventUids(List<String> eventUids);

    List<AcademicEventReadModel> findAllEventReadModels();

    List<AcademicEventReadModel> findEventsByDate(LocalDate startDate, LocalDate endDate);
}
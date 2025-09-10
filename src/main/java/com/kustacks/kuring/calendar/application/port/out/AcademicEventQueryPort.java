package com.kustacks.kuring.calendar.application.port.out;

import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.domain.AcademicEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AcademicEventQueryPort {

    List<AcademicEvent> findAll();

    List<AcademicEventReadModel> findAllEventReadModels();

    Map<String, AcademicEvent> findEventsInEventUidsAsMap(List<String> eventUids);

    List<AcademicEvent> findEventsByEventUids(List<String> eventUids);

    List<AcademicEventReadModel> findEventsBetweenDate(LocalDate startDate, LocalDate endDate);

    List<AcademicEventReadModel> findEventsAfter(LocalDate startDate);

    List<AcademicEventReadModel> findEventsBefore(LocalDate endDate);
}

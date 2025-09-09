package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.application.port.out.AcademicEventCommandPort;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class AcademicEventPersistenceAdapter implements AcademicEventQueryPort, AcademicEventCommandPort {

    private final AcademicEventRepository academicEventRepository;
    private final AcademicEventJdbcRepository academicEventJdbcRepository;

    @Override
    public void saveAll(List<AcademicEvent> academicEvents) {
        academicEventJdbcRepository.saveAll(academicEvents);
    }

    @Override
    public void updateAll(List<AcademicEvent> academicEvents) {
        academicEventJdbcRepository.updateAll(academicEvents);
    }

    @Override
    public List<AcademicEvent> findAll() {
        return academicEventRepository.findAll();
    }

    @Override
    public List<AcademicEventReadModel> findAllEventReadModels() {
        return academicEventRepository.findAllEventReadModels();
    }

    @Override
    public Map<String, AcademicEvent> findEventsInEventUidsAsMap(List<String> eventUids) {
        return findEventsByEventUids(eventUids)
                .stream()
                .collect(Collectors.toMap(AcademicEvent::getEventUid,
                        academicEvent -> academicEvent));
    }

    @Override
    public List<AcademicEvent> findEventsByEventUids(List<String> eventUids) {
        return academicEventRepository.findByEventUids(eventUids);
    }

    @Override
    public List<AcademicEventReadModel> findEventsBetweenDate(LocalDate startDate, LocalDate endDate) {
        return academicEventRepository.findEventsBetweenDate(startDate, endDate);
    }

    @Override
    public List<AcademicEventReadModel> findEventsAfter(LocalDate startDate) {
        return academicEventRepository.findEventsAfter(startDate);
    }

    @Override
    public List<AcademicEventReadModel> findEventsBefore(LocalDate endDate) {
        return academicEventRepository.findEventsBefore(endDate);
    }
}

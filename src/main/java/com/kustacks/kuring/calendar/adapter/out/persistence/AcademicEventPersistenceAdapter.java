package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.application.port.out.AcademicEventCommandPort;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

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
    public Map<String, AcademicEvent> findAllInEventUidsAsMap(List<String> eventUids) {
        return findAllByEventUids(eventUids)
                .stream()
                .collect(Collectors.toMap(AcademicEvent::getEventUid,
                        academicEventReadModel -> academicEventReadModel));
    }

    @Override
    public List<AcademicEvent> findAllByEventUids(List<String> eventUids) {
        return academicEventRepository.findAllByEventUids(eventUids);
    }
}

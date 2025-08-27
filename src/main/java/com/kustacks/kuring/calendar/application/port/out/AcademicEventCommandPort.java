package com.kustacks.kuring.calendar.application.port.out;

import com.kustacks.kuring.calendar.domain.AcademicEvent;

import java.util.List;

public interface AcademicEventCommandPort {

    void saveAll(List<AcademicEvent> academicEvents);

    void updateAll(List<AcademicEvent> academicEvents);

    void deleteAll(List<Long> oldEventIds);
}

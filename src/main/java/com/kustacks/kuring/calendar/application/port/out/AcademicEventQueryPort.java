package com.kustacks.kuring.calendar.application.port.out;

import com.kustacks.kuring.calendar.domain.AcademicEvent;

import java.util.List;
import java.util.Map;

public interface AcademicEventQueryPort {

    Map<String, AcademicEvent> findAllInEventUidsAsMap(List<String> eventUids);

    List<AcademicEvent> findAllByEventUids(List<String> eventUids);
}

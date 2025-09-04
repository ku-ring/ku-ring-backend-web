package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.domain.AcademicEvent;

import java.util.List;

interface AcademicEventQueryRepository {

    List<AcademicEvent> findAllByEventUids(List<String> eventUids);
}
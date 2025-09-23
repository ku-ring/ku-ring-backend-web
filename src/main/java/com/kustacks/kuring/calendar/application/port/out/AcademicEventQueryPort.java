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

    /**
     * 지정된 날짜에 시작하거나 종료하는 알림 가능한 학사일정들을 조회합니다.
     * (DB 접근 최적화 - 한번에 조회)
     *
     * @param date 조회할 날짜
     * @return notifyEnabled가 true이고 해당 날짜에 시작하거나 종료하는 일정들
     */
    List<AcademicEventReadModel> findTodayEvents(LocalDate date);
}

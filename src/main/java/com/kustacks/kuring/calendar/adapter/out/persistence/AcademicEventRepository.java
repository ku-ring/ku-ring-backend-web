package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AcademicEventRepository extends JpaRepository<AcademicEvent, Long> {

    @Query("SELECT a FROM academic_event a WHERE a.eventUid IN :eventUids")
    List<AcademicEvent> findAllByEventUids(@Param("eventUids") List<String> eventUids);
}

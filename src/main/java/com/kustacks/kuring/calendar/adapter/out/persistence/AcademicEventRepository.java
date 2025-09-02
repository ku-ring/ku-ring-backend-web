package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicEventRepository extends JpaRepository<AcademicEvent, Long>, AcademicEventQueryRepository {
}

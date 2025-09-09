package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.application.port.out.dto.QAcademicEventReadModel;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.kustacks.kuring.calendar.domain.QAcademicEvent.academicEvent;

@RequiredArgsConstructor
class AcademicEventQueryRepositoryImpl implements AcademicEventQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AcademicEvent> findByEventUids(List<String> eventUids) {
        return queryFactory
                .select(academicEvent)
                .from(academicEvent)
                .where(academicEvent.eventUid.in(eventUids))
                .fetch();
    }

    @Override
    public List<AcademicEventReadModel> findAllEventReadModels() {
        return queryFactorySelectingReadModel()
                .fetch();
    }

    @Override
    public List<AcademicEventReadModel> findEventsAfter(LocalDate startDate) {
        return queryFactorySelectingReadModel()
                .where(buildBooleanExpressionByDates(startDate, null))
                .fetch();
    }


    @Override
    public List<AcademicEventReadModel> findEventsBefore(LocalDate endDate) {
        return queryFactorySelectingReadModel()
                .where(buildBooleanExpressionByDates(null, endDate))
                .fetch();
    }

    @Override
    public List<AcademicEventReadModel> findEventsBetweenDate(LocalDate startDate, LocalDate endDate) {
        return queryFactorySelectingReadModel()
                .where(buildBooleanExpressionByDates(startDate, endDate))
                .fetch();
    }

    private JPAQuery<AcademicEventReadModel> queryFactorySelectingReadModel() {
        return queryFactory
                .select(new QAcademicEventReadModel(
                        academicEvent.id,
                        academicEvent.eventUid,
                        academicEvent.summary,
                        academicEvent.description,
                        academicEvent.category,
                        academicEvent.transparent,
                        academicEvent.sequence,
                        academicEvent.notifyEnabled,
                        academicEvent.startTime,
                        academicEvent.endTime
                ))
                .from(academicEvent);
    }

    private BooleanExpression buildBooleanExpressionByDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return academicEvent.startTime.lt(endDate.plusDays(1L).atStartOfDay());
        }
        if (endDate == null) {
            return academicEvent.endTime.goe(startDate.atStartOfDay());
        }
        return academicEvent.startTime.lt(endDate.plusDays(1L).atStartOfDay())
                .and(academicEvent.endTime.goe(startDate.atStartOfDay()));
    }
}
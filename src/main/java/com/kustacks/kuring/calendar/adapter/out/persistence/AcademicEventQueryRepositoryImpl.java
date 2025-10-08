package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.application.port.out.dto.QAcademicEventReadModel;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.kustacks.kuring.calendar.domain.QAcademicEvent.academicEvent;

@RequiredArgsConstructor
class AcademicEventQueryRepositoryImpl implements AcademicEventQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<AcademicEvent> findByEventUids(List<String> eventUids) {
        if (eventUids == null || eventUids.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .select(academicEvent)
                .from(academicEvent)
                .where(academicEvent.eventUid.in(eventUids))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicEventReadModel> findAllEventReadModels() {
        return queryFactorySelectingReadModel()
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicEventReadModel> findEventsByDate(LocalDate startDate, LocalDate endDate) {
        return queryFactorySelectingReadModel()
                .where(
                        isEndTimeGoe(startDate),
                        isStartTimeLt(endDate)
                )
                .fetch();
    }

    private JPAQuery<AcademicEventReadModel> queryFactorySelectingReadModel() {
        return queryFactory
                .select(new QAcademicEventReadModel(
                        academicEvent.id,
                        academicEvent.eventUid,
                        academicEvent.summary,
                        academicEvent.description,
                        academicEvent.category.stringValue(),
                        academicEvent.transparent,
                        academicEvent.sequence,
                        academicEvent.notifyEnabled,
                        academicEvent.startTime,
                        academicEvent.endTime
                ))
                .from(academicEvent);
    }

    private BooleanExpression isEndTimeGoe(LocalDate startDate) {
        return startDate != null ? academicEvent.endTime.goe(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression isStartTimeLt(LocalDate endDate) {
        return endDate != null ? academicEvent.startTime.lt(endDate.plusDays(1L).atStartOfDay()) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicEventReadModel> findTodayEvents(LocalDate date) {
        return queryFactorySelectingReadModel()
                .where(
                        academicEvent.notifyEnabled.isTrue(),
                        academicEvent.startTime.between(
                                date.atStartOfDay(),
                                date.atTime(23, 59, 59, 999_999_999)
                        ).or(
                                academicEvent.endTime.between(
                                        date.atStartOfDay(),
                                        date.atTime(23, 59, 59, 999_999_999)
                                )
                        )
                )
                .fetch();
    }
}
package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.calendar.domain.QAcademicEvent.academicEvent;

@RequiredArgsConstructor
class AcademicEventQueryRepositoryImpl implements AcademicEventQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AcademicEvent> findAllByEventUids(List<String> eventUids) {
        return queryFactory
                .select(academicEvent)
                .from(academicEvent)
                .where(academicEvent.eventUid.in(eventUids))
                .fetch();
    }
}
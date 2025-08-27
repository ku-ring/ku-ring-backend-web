package com.kustacks.kuring.calendar.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustacks.kuring.calendar.domain.QAcademicEvent.academicEvent;

@RequiredArgsConstructor
public class AcademicEventQueryRepositoryImpl implements AcademicEventQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional
    public void deleteAll(List<Long> oldEventIds) {
        queryFactory.delete(academicEvent)
                .where(academicEvent.id.in(oldEventIds))
                .execute();
    }
}

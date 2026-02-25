package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.kustacks.kuring.club.domain.QClub.club;

@RequiredArgsConstructor
class ClubQueryRepositoryImpl implements ClubQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Club> findClubsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return queryFactory.selectFrom(club)
                .where(
                        club.isAlways.isFalse(),
                        recruitEndAtGoe(start),
                        recruitEndAtLt(end)
                )
                .fetch();
    }

    private BooleanExpression recruitEndAtGoe(LocalDateTime start) {
        return start != null ? club.recruitEndAt.goe(start) : null;
    }

    private BooleanExpression recruitEndAtLt(LocalDateTime end) {
        return end != null ? club.recruitEndAt.lt(end) : null;
    }
}

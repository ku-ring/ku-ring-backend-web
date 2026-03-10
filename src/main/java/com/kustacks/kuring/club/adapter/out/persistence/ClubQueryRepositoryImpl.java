package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.dto.ClubDetailReadModel;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.application.port.out.dto.QClubReadModel;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.club.domain.ClubSnsType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.club.domain.QClub.club;
import static com.kustacks.kuring.club.domain.QClubSns.clubSns;

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

    @Override
    @Transactional(readOnly = true)
    public List<ClubReadModel> searchClubs(
            ClubCategory category,
            List<ClubDivision> divisions
    ) {
        return queryFactory
                .select(new QClubReadModel(
                        club.id,
                        club.name,
                        club.summary,
                        club.iconImagePath,
                        club.category,
                        club.division,
                        club.recruitStartAt,
                        club.recruitEndAt
                ))
                .from(club)
                .where(
                        categoryEq(category),
                        divisionIn(divisions)
                )
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubReadModel> findClubsByIds(List<Long> ids) {
        return queryFactory
                .select(new QClubReadModel(
                        club.id,
                        club.name,
                        club.summary,
                        club.iconImagePath,
                        club.category,
                        club.division,
                        club.recruitStartAt,
                        club.recruitEndAt
                ))
                .from(club)
                .where(club.id.in(ids))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubDetailReadModel> findClubDetailById(Long id) {

        List<Tuple> tuples = queryFactory
                .select(
                        club.id,
                        club.name,
                        club.summary,
                        club.category,
                        club.division,
                        club.description,
                        club.qualifications,
                        club.recruitStartAt,
                        club.recruitEndAt,
                        club.isAlways,
                        club.applyUrl,
                        club.posterImagePath,
                        club.building,
                        club.room,
                        club.lon,
                        club.lat,
                        clubSns.type,
                        clubSns.url
                )
                .from(club)
                .leftJoin(club.homepageUrls, clubSns)
                .where(club.id.eq(id))
                .fetch();

        if (tuples.isEmpty()) {
            return Optional.empty();
        }

        Tuple first = tuples.get(0);

        String instagram = null;
        String youtube = null;
        String etc = null;

        for (Tuple t : tuples) {
            ClubSnsType type = t.get(clubSns.type);
            String url = t.get(clubSns.url);

            if (type == null) continue;

            switch (type) {
                case INSTAGRAM -> instagram = url;
                case YOUTUBE -> youtube = url;
                case ETC -> etc = url;
            }
        }

        return Optional.of(
                new ClubDetailReadModel(
                        first.get(club.id),
                        first.get(club.name),
                        first.get(club.summary),
                        first.get(club.category),
                        first.get(club.division),
                        instagram,
                        youtube,
                        etc,
                        first.get(club.description),
                        first.get(club.qualifications),
                        first.get(club.recruitStartAt),
                        first.get(club.recruitEndAt),
                        first.get(club.isAlways),
                        first.get(club.applyUrl),
                        first.get(club.posterImagePath),
                        first.get(club.building),
                        first.get(club.room),
                        first.get(club.lon),
                        first.get(club.lat)
                )
        );
    }

    private BooleanExpression categoryEq(ClubCategory category) {
        return category != null ? club.category.eq(category) : null;
    }

    private BooleanExpression divisionIn(List<ClubDivision> divisions) {
        return divisions != null && !divisions.isEmpty()
                ? club.division.in(divisions)
                : null;
    }

}

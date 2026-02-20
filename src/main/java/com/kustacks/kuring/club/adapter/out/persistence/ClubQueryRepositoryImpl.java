package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.application.port.out.dto.QClubReadModel;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.club.domain.ClubRecruitmentStatus;
import com.kustacks.kuring.club.domain.ClubSnsType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
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
    @Transactional(readOnly = true)
    public List<ClubReadModel> searchClubs(
            String category,
            List<String> divisions,
            String cursor,
            int size,
            String sortBy
    ) {

        return queryFactory
                .select(new QClubReadModel(
                        club.id,
                        club.name,
                        club.summary,
                        club.posterImagePath,
                        club.category,
                        club.division,
                        club.recruitStartAt,
                        club.recruitEndAt
                ))
                .from(club)
                .where(
                        categoryEq(category),
                        divisionIn(divisions),
                        idAfterCursor(cursor)
                )
                .orderBy(getOrderSpecifiers(sortBy))
                .limit(size)
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public int countClubs(String category, List<String> divisions) {

        Long count = queryFactory
                .select(club.count())
                .from(club)
                .where(
                        categoryEq(category),
                        divisionIn(divisions)
                )
                .fetchOne();

        return count == null ? 0 : count.intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubDetailDto> findClubDetailById(Long id) {

        LocalDateTime now = LocalDateTime.now();

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

        ClubRecruitmentStatus recruitmentStatus = calculateRecruitmentStatus(
                first.get(club.recruitStartAt),
                first.get(club.recruitEndAt),
                first.get(club.isAlways),
                now
        );

        return Optional.of(
                new ClubDetailDto(
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
                        recruitmentStatus,
                        first.get(club.recruitStartAt),
                        first.get(club.recruitEndAt),
                        first.get(club.applyUrl),
                        first.get(club.posterImagePath),
                        first.get(club.building),
                        first.get(club.room),
                        first.get(club.lon),
                        first.get(club.lat)
                )
        );
    }


    private BooleanExpression categoryEq(String category) {
        if (category == null) return null;
        return club.category.eq(ClubCategory.fromName(category));
    }

    private BooleanExpression divisionIn(List<String> divisions) {
        if (divisions == null || divisions.isEmpty()) return null;

        return club.division.in(
                divisions.stream()
                        .map(ClubDivision::fromName)
                        .toList()
        );
    }

    private BooleanExpression idAfterCursor(String cursor) {
        if (cursor == null || cursor.equals("0")) return null;
        return club.id.gt(Long.parseLong(cursor));
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sortBy) {

        if ("recruitEndDate".equals(sortBy)) {
            return new OrderSpecifier[]{
                    club.recruitEndAt.asc(),
                    club.id.asc()
            };
        }

        return new OrderSpecifier[]{
                club.name.asc(),
                club.id.asc()
        };
    }

    private ClubRecruitmentStatus calculateRecruitmentStatus(
            LocalDateTime start,
            LocalDateTime end,
            Boolean isAlways,
            LocalDateTime now
    ) {

        if (Boolean.TRUE.equals(isAlways)) {
            return ClubRecruitmentStatus.ALWAYS;
        }

        if (start != null && now.isBefore(start)) {
            return ClubRecruitmentStatus.BEFORE;
        }

        if (end != null && now.isAfter(end)) {
            return ClubRecruitmentStatus.CLOSED;
        }

        return ClubRecruitmentStatus.RECRUITING;
    }


}

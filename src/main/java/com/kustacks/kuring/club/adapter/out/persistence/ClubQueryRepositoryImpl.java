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
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
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
            String sortBy,
            LocalDateTime now
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
                        cursorCondition(sortBy, cursor, now)
                )
                .orderBy(getOrderSpecifiers(sortBy, now))
                .limit(size)
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public int countClubsByCategoryAndDivisions(String category, List<String> divisions) {

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

    private BooleanExpression cursorCondition(String sortBy, String cursor, LocalDateTime now) {

        if (cursor == null || cursor.equals("0")) return null;

        try {

            String[] parts = cursor.split("\\|");

            return switch (sortBy) {

                case "name" -> {
                    if (parts.length < 2) yield null;

                    String lastName = parts[0];
                    Long lastId = Long.parseLong(parts[1]);

                    yield club.name.gt(lastName)
                            .or(
                                    club.name.eq(lastName)
                                            .and(club.id.gt(lastId))
                            );
                }

                case "recruitEndDate" -> {
                    if (parts.length < 3) yield null;

                    int lastGroup = Integer.parseInt(parts[0]);
                    String lastDateStr = parts[1];
                    Long lastId = Long.parseLong(parts[2]);

                    NumberExpression<Integer> currentGroup = recruitmentGroup(now);

                    BooleanExpression groupCondition = currentGroup.gt(lastGroup);

                    BooleanExpression sameGroupCondition;

                    if ("null".equals(lastDateStr)) {
                        sameGroupCondition = currentGroup.eq(lastGroup)
                                .and(club.id.gt(lastId));
                    } else {
                        LocalDateTime lastDate = LocalDateTime.parse(lastDateStr);
                        sameGroupCondition = currentGroup.eq(lastGroup)
                                .and(
                                        club.recruitEndAt.gt(lastDate)
                                                .or(
                                                        club.recruitEndAt.eq(lastDate)
                                                                .and(club.id.gt(lastId))
                                                )
                                );
                    }
                    yield groupCondition.or(sameGroupCondition);

                }

                default -> {
                    Long lastId = Long.parseLong(cursor);
                    yield club.id.gt(lastId);
                }
            };

        } catch (Exception e) {
            return null;
        }
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sortBy, LocalDateTime now) {

        return switch (sortBy) {

            case "name" -> new OrderSpecifier[]{
                    club.name.asc(),
                    club.id.asc()
            };

            case "recruitEndDate" -> {

                var statusOrder = new CaseBuilder()
                        .when(club.recruitEndAt.isNull()).then(2)
                        .when(club.recruitEndAt.lt(now)).then(1)
                        .otherwise(0);

                yield new OrderSpecifier[]{
                        statusOrder.asc(),
                        club.recruitEndAt.asc().nullsLast(),
                        club.id.asc()
                };
            }

            default -> new OrderSpecifier[]{
                    club.id.asc()
            };
        };
    }

    private NumberExpression<Integer> recruitmentGroup(LocalDateTime now) {
        return new CaseBuilder()
                .when(club.recruitEndAt.isNull()).then(2)
                .when(club.recruitEndAt.lt(now)).then(1)
                .otherwise(0);
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

package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.application.port.out.dto.QClubReadModel;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustacks.kuring.club.domain.QClub.club;

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
                        club.category.stringValue(),
                        club.division.stringValue(),
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

}

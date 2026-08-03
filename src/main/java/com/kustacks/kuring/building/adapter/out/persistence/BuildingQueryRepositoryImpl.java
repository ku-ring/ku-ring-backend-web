package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.Building;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.building.domain.QBuilding.building;
import static com.kustacks.kuring.building.domain.QBuildingSearchKeyword.buildingSearchKeyword;

@RequiredArgsConstructor
class BuildingQueryRepositoryImpl implements BuildingQueryRepository {

    private static final char LIKE_ESCAPE = '\\';

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Building> searchByKeyword(String keyword) {
        return queryFactory
                .selectFrom(building)
                .leftJoin(building.keywords, buildingSearchKeyword)
                .where(keywordContains(keyword))
                .distinct()
                .orderBy(building.id.asc())
                .fetch();
    }

    private BooleanExpression keywordContains(String keyword) {
        String pattern = "%" + escapeLikePattern(keyword) + "%";

        return building.name.likeIgnoreCase(pattern, LIKE_ESCAPE)
                .or(building.address.likeIgnoreCase(pattern, LIKE_ESCAPE))
                .or(buildingSearchKeyword.keyword.likeIgnoreCase(pattern, LIKE_ESCAPE));
    }

    private String escapeLikePattern(String keyword) {
        return keyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}

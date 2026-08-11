package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.Building;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.building.domain.QBuilding.building;

@RequiredArgsConstructor
class BuildingQueryRepositoryImpl implements BuildingQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Building> findAllSortedByDisplayOrder() {
        return queryFactory
                .selectFrom(building)
                .orderBy(
                        new CaseBuilder()
                                .when(building.displayOrder.isNull())
                                .then(1)
                                .otherwise(0)
                                .asc(),
                        building.displayOrder.asc(),
                        building.id.asc()
                )
                .fetch();
    }

    @Override
    public List<Building> searchByKeyword(String keyword) {
        return queryFactory
                .selectFrom(building)
                .where(
                        building.name.containsIgnoreCase(keyword)
                                .or(building.address.containsIgnoreCase(keyword))
                                .or(building.keywords.any().keyword.containsIgnoreCase(keyword))
                )
                .orderBy(
                        new CaseBuilder()
                                .when(building.displayOrder.isNull())
                                .then(1)
                                .otherwise(0)
                                .asc(),
                        building.displayOrder.asc(),
                        building.id.asc()
                )
                .fetch();
    }
}

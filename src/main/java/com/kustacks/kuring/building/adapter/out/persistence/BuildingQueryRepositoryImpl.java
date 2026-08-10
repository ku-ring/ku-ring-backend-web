package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.Building;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.building.domain.QBuilding.building;

@RequiredArgsConstructor
class BuildingQueryRepositoryImpl implements BuildingQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Building> searchByKeyword(String keyword) {
        return queryFactory
                .selectFrom(building)
                .where(
                        building.name.containsIgnoreCase(keyword)
                                .or(building.address.containsIgnoreCase(keyword))
                                .or(building.keywords.any().keyword.containsIgnoreCase(keyword))
                )
                .orderBy(building.displayOrder.asc(), building.id.asc())
                .fetch();
    }
}

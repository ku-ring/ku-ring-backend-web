package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.CampusPlace;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.kustacks.kuring.building.domain.QBuilding.building;
import static com.kustacks.kuring.building.domain.QCampusPlace.campusPlace;
import static com.kustacks.kuring.building.domain.QCampusPlaceCategory.campusPlaceCategory;
import static com.kustacks.kuring.building.domain.QOperatingHours.operatingHours;

@RequiredArgsConstructor
class CampusPlaceQueryRepositoryImpl implements CampusPlaceQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CampusPlace> findByFilterCategories(List<String> categoryCodes) {
        return queryFactory
                .selectFrom(campusPlace)
                .join(campusPlace.building, building).fetchJoin()
                .join(campusPlace.category, campusPlaceCategory).fetchJoin()
                .leftJoin(campusPlace.operatingHours, operatingHours).fetchJoin()
                .where(
                        campusPlaceCategory.code.in(categoryCodes),
                        campusPlaceCategory.filterEnabled.isTrue()
                )
                .distinct()
                .orderBy(campusPlace.displayOrder.asc(), campusPlace.id.asc())
                .fetch();
    }

    @Override
    public List<CampusPlace> findByBuildingId(Long buildingId) {
        return queryFactory
                .selectFrom(campusPlace)
                .join(campusPlace.building, building).fetchJoin()
                .join(campusPlace.category, campusPlaceCategory).fetchJoin()
                .leftJoin(campusPlace.operatingHours, operatingHours).fetchJoin()
                .where(campusPlace.building.id.eq(buildingId))
                .distinct()
                .orderBy(campusPlace.displayOrder.asc(), campusPlace.id.asc())
                .fetch();
    }
}

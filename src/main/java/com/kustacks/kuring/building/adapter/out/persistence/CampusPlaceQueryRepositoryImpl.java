package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.CampusPlace;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustacks.kuring.building.domain.QBuilding.building;
import static com.kustacks.kuring.building.domain.QCampusPlace.campusPlace;
import static com.kustacks.kuring.building.domain.QCampusPlaceCategory.campusPlaceCategory;
import static com.kustacks.kuring.building.domain.QOperatingHours.operatingHours;

@RequiredArgsConstructor
class CampusPlaceQueryRepositoryImpl implements CampusPlaceQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<CampusPlace> searchByKeyword(String keyword) {
        List<CampusPlace> places = queryFactory
                .selectFrom(campusPlace)
                .join(campusPlace.building, building).fetchJoin()
                .join(campusPlace.category, campusPlaceCategory).fetchJoin()
                .where(matchesKeyword(keyword))
                .orderBy(
                        new CaseBuilder()
                                .when(building.displayOrder.isNull())
                                .then(1)
                                .otherwise(0)
                                .asc(),
                        building.displayOrder.asc(),
                        building.id.asc(),
                        campusPlace.displayOrder.asc(),
                        campusPlace.id.asc()
                )
                .fetch();

        fetchOperatingHours(places);
        return places;
    }

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

    private BooleanExpression matchesKeyword(String keyword) {
        return campusPlace.name.containsIgnoreCase(keyword)
                .or(campusPlaceCategory.korName.containsIgnoreCase(keyword));
    }

    private void fetchOperatingHours(List<CampusPlace> places) {
        if (places.isEmpty()) {
            return;
        }

        List<Long> campusPlaceIds = places.stream()
                .map(CampusPlace::getId)
                .toList();

        queryFactory
                .selectFrom(campusPlace)
                .leftJoin(campusPlace.operatingHours, operatingHours).fetchJoin()
                .where(campusPlace.id.in(campusPlaceIds))
                .distinct()
                .fetch();
    }
}

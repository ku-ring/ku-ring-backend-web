package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHours;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("리포지토리 : 캠퍼스맵")
class CampusMapPersistenceAdapterTest extends IntegrationTestSupport {

    @Autowired
    private CampusMapQueryPort campusMapQueryPort;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private CampusPlaceCategoryRepository categoryRepository;

    @Autowired
    private CampusPlaceRepository campusPlaceRepository;

    @Test
    @DisplayName("건물명, 주소, 등록된 검색어로 건물을 검색한다")
    void search_buildings_by_name_address_and_keyword() {
        // given
        Building lawBuilding = new Building(
                "법학관",
                "서울특별시 광진구 능동로 120",
                37.54174,
                127.07649,
                null
        );
        lawBuilding.addSearchKeyword("종강");
        buildingRepository.saveAndFlush(lawBuilding);

        // when & then
        assertThat(campusMapQueryPort.searchBuildings("법학"))
                .extracting(Building::getName)
                .containsExactly("법학관");
        assertThat(campusMapQueryPort.searchBuildings("능동로"))
                .extracting(Building::getName)
                .containsExactly("법학관");
        assertThat(campusMapQueryPort.searchBuildings("종강"))
                .extracting(Building::getName)
                .containsExactly("법학관");
    }

    @Test
    @DisplayName("노출 카테고리만 순서대로 조회하고 건물 상세에서는 비노출 시설도 포함한다")
    void find_filter_categories_and_campus_places() {
        // given
        Building building = buildingRepository.saveAndFlush(new Building(
                "학생회관",
                "서울특별시 광진구 능동로 120",
                37.5412,
                127.0784,
                null
        ));
        CampusPlaceCategory cafe = categoryRepository.save(
                new CampusPlaceCategory("cafe", "카페", 2, true)
        );
        CampusPlaceCategory printer = categoryRepository.save(
                new CampusPlaceCategory("printer", "프린터", 1, true)
        );
        CampusPlaceCategory mainFacility = categoryRepository.save(
                new CampusPlaceCategory("main_facility", "주요시설", 100, false)
        );

        CampusPlace cafePlace = campusPlace(building, cafe, "학생회관 카페", 1);
        CampusPlace printerPlace = campusPlace(building, printer, "학생회관 프린터", 2);
        printerPlace.addOperatingHours(new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        ));
        CampusPlace studentCouncil = campusPlace(building, mainFacility, "총학생회", 0);
        campusPlaceRepository.saveAllAndFlush(List.of(cafePlace, printerPlace, studentCouncil));

        // when
        List<CampusPlaceCategory> categories = campusMapQueryPort.findFilterCategories();
        List<CampusPlace> filteredPlaces = campusMapQueryPort.findCampusPlacesByCategories(
                List.of("printer", "cafe", "main_facility")
        );
        List<CampusPlace> buildingPlaces = campusMapQueryPort.findCampusPlacesByBuildingId(
                building.getId()
        );

        // then
        assertThat(categories)
                .extracting(CampusPlaceCategory::getCode)
                .containsExactly("printer", "cafe");
        assertThat(filteredPlaces)
                .extracting(CampusPlace::getName)
                .containsExactly("학생회관 카페", "학생회관 프린터");
        assertThat(filteredPlaces.get(1).getOperatingHours()).hasSize(1);
        assertThat(filteredPlaces.get(1).getBuilding().getName()).isEqualTo("학생회관");
        assertThat(buildingPlaces)
                .extracting(CampusPlace::getName)
                .containsExactly("총학생회", "학생회관 카페", "학생회관 프린터");
    }

    private CampusPlace campusPlace(
            Building building,
            CampusPlaceCategory category,
            String name,
            int displayOrder
    ) {
        return new CampusPlace(
                building,
                category,
                name,
                null,
                CampusPlaceLocationType.INDOOR,
                "1F",
                null,
                null,
                null,
                displayOrder
        );
    }
}

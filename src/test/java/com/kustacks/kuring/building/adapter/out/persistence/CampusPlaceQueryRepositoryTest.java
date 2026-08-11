package com.kustacks.kuring.building.adapter.out.persistence;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("리포지토리 : CampusPlaceQueryRepository")
class CampusPlaceQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private CampusPlaceCategoryRepository categoryRepository;

    @Autowired
    private CampusPlaceRepository campusPlaceRepository;

    @DisplayName("시설명과 카테고리명으로 캠퍼스 시설을 검색한다")
    @ParameterizedTest
    @ValueSource(strings = {"푸드코트", "식당"})
    void search_campus_places_by_keyword(String keyword) {
        // given
        Building building = buildingRepository.saveAndFlush(new Building(
                "학생회관",
                "서울특별시 광진구 능동로 120",
                37.5412,
                127.0784,
                null
        ));
        CampusPlaceCategory restaurant = categoryRepository.save(
                new CampusPlaceCategory("test_restaurant", "식당", 1, true)
        );
        CampusPlaceCategory printer = categoryRepository.save(
                new CampusPlaceCategory("test_printer", "프린터", 2, true)
        );
        CampusPlace restaurantPlace = campusPlace(building, restaurant, "학생회관 푸드코트", 1);
        restaurantPlace.addOperatingHours(new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        ));
        campusPlaceRepository.saveAllAndFlush(List.of(
                restaurantPlace,
                campusPlace(building, printer, "학생회관 프린터", 2)
        ));

        // when
        List<CampusPlace> result = campusPlaceRepository.searchByKeyword(keyword);

        // then
        assertAll(
                () -> assertThat(result)
                        .extracting(CampusPlace::getName)
                        .containsExactly("학생회관 푸드코트"),
                () -> assertThat(result.get(0).getOperatingHours()).hasSize(1)
        );
    }

    @Test
    @DisplayName("필터에 노출되는 카테고리의 캠퍼스 시설을 노출 순서대로 조회한다")
    void find_campus_places_by_filter_categories() {
        // given
        Building building = buildingRepository.saveAndFlush(new Building(
                "학생회관",
                "서울특별시 광진구 능동로 120",
                37.5412,
                127.0784,
                null
        ));
        CampusPlaceCategory cafe = categoryRepository.save(
                new CampusPlaceCategory("test_cafe", "카페", 2, true)
        );
        CampusPlaceCategory printer = categoryRepository.save(
                new CampusPlaceCategory("test_printer", "프린터", 1, true)
        );
        CampusPlaceCategory mainFacility = categoryRepository.save(
                new CampusPlaceCategory("test_main_facility", "주요시설", 100, false)
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
        CampusPlace studentCouncil = campusPlace(building, mainFacility, "총학생회", 3);
        campusPlaceRepository.saveAllAndFlush(List.of(cafePlace, printerPlace, studentCouncil));

        // when
        List<CampusPlace> result = campusPlaceRepository.findByFilterCategories(
                List.of("test_printer", "test_cafe", "test_main_facility")
        );

        // then
        assertAll(
                () -> assertThat(result)
                        .extracting(CampusPlace::getName)
                        .containsExactly("학생회관 카페", "학생회관 프린터"),
                () -> assertThat(result.get(1).getOperatingHours()).hasSize(1),
                () -> assertThat(result.get(1).getBuilding().getName()).isEqualTo("학생회관")
        );
    }

    @Test
    @DisplayName("건물에 등록된 모든 캠퍼스 시설을 노출 순서대로 조회한다")
    void find_campus_places_by_building_id() {
        // given
        Building building = buildingRepository.saveAndFlush(new Building(
                "학생회관",
                "서울특별시 광진구 능동로 120",
                37.5412,
                127.0784,
                null
        ));
        CampusPlaceCategory mainFacility = categoryRepository.save(
                new CampusPlaceCategory("test_main_facility", "주요시설", 100, false)
        );
        CampusPlaceCategory printer = categoryRepository.save(
                new CampusPlaceCategory("test_printer", "프린터", 1, true)
        );
        CampusPlace studentCouncil = campusPlace(building, mainFacility, "총학생회", 2);
        CampusPlace printerPlace = campusPlace(building, printer, "학생회관 프린터", 1);
        printerPlace.addOperatingHours(new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        ));
        campusPlaceRepository.saveAllAndFlush(List.of(studentCouncil, printerPlace));

        // when
        List<CampusPlace> result = campusPlaceRepository.findByBuildingId(building.getId());

        // then
        assertAll(
                () -> assertThat(result)
                        .extracting(CampusPlace::getName)
                        .containsExactly("학생회관 프린터", "총학생회"),
                () -> assertThat(result.get(0).getOperatingHours()).hasSize(1),
                () -> assertThat(result.get(1).getCategory().isFilterEnabled()).isFalse()
        );
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

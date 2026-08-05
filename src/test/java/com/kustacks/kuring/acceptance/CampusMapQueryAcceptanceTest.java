package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.building.adapter.out.persistence.BuildingRepository;
import com.kustacks.kuring.building.adapter.out.persistence.CampusPlaceCategoryRepository;
import com.kustacks.kuring.building.adapter.out.persistence.CampusPlaceRepository;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static com.kustacks.kuring.acceptance.CampusMapStep.assertBuildingListResponse;
import static com.kustacks.kuring.acceptance.CampusMapStep.assertCampusPlaceListResponse;
import static com.kustacks.kuring.acceptance.CampusMapStep.assertCategoryListResponse;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestBuildingSearch;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestBuildings;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestCampusPlaces;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestCategories;

@DisplayName("인수 : 캠퍼스맵 조회 API")
@TestPropertySource(properties = "campus-map.source=database")
class CampusMapQueryAcceptanceTest extends IntegrationTestSupport {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private CampusPlaceCategoryRepository categoryRepository;

    @Autowired
    private CampusPlaceRepository campusPlaceRepository;

    @Test
    @DisplayName("필터에 노출되는 카테고리 목록을 조회한다")
    void getCategories_success() {
        // given
        categoryRepository.saveAllAndFlush(List.of(
                new CampusPlaceCategory("printer", "프린터", 1, true),
                new CampusPlaceCategory("cafe", "카페", 2, true),
                new CampusPlaceCategory("main_facility", "주요시설", 3, false)
        ));

        // when
        var response = requestCategories();

        // then
        assertCategoryListResponse(response, "printer", "cafe");
    }

    @Test
    @DisplayName("등록된 전체 건물 목록을 조회한다")
    void getBuildings_success() {
        // given
        buildingRepository.saveAllAndFlush(List.of(
                building("행정관", 37.54241, 127.07382),
                building("경영관", 37.54196, 127.07531)
        ));

        // when
        var response = requestBuildings();

        // then
        assertBuildingListResponse(response, "행정관", "경영관");
    }

    @Test
    @DisplayName("건물에 등록된 키워드로 건물을 검색한다")
    void searchBuildings_success() {
        // given
        Building lawBuilding = building("법학관", 37.54174, 127.07649);
        lawBuilding.addSearchKeyword("종강");
        buildingRepository.saveAndFlush(lawBuilding);
        buildingRepository.saveAndFlush(building("학생회관", 37.5412, 127.0784));

        // when
        var response = requestBuildingSearch("종강");

        // then
        assertBuildingListResponse(response, "법학관");
    }

    @Test
    @DisplayName("카테고리에 해당하는 캠퍼스 시설을 조회한다")
    void getCampusPlaces_success() {
        // given
        Building building = buildingRepository.saveAndFlush(building("학생회관", 37.5412, 127.0784));
        CampusPlaceCategory category = categoryRepository.saveAndFlush(
                new CampusPlaceCategory("test_printer", "프린터", 1, true)
        );
        campusPlaceRepository.saveAndFlush(new CampusPlace(
                building,
                category,
                "학생회관 프린터",
                null,
                CampusPlaceLocationType.INDOOR,
                "1F",
                "라운지 안쪽",
                3,
                null,
                1
        ));

        // when
        var response = requestCampusPlaces("test_printer");

        // then
        assertCampusPlaceListResponse(
                response,
                "학생회관 프린터",
                "test_printer",
                "학생회관"
        );
    }

    private Building building(String name, Double latitude, Double longitude) {
        return new Building(
                name,
                "서울특별시 광진구 능동로 120",
                latitude,
                longitude,
                null
        );
    }
}

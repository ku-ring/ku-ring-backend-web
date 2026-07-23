package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.CampusMapStep.assertBadRequest;
import static com.kustacks.kuring.acceptance.CampusMapStep.assertNotFoundResponse;
import static com.kustacks.kuring.acceptance.CampusMapStep.assertSuccessfulListResponse;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestBuildingDetail;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestBuildingSearch;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestBuildings;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestCampusPlaces;
import static com.kustacks.kuring.acceptance.CampusMapStep.requestCategories;

@DisplayName("인수 : 캠퍼스맵")
class CampusMapAcceptanceTest extends IntegrationTestSupport {

    @Test
    @DisplayName("캠퍼스맵 카테고리 목록을 조회한다")
    void getCategories_success() {
        // when
        var response = requestCategories();

        // then
        assertSuccessfulListResponse(response, "data.categories");
    }

    @Test
    @DisplayName("캠퍼스 전체 건물 목록을 조회한다")
    void getBuildings_success() {
        // when
        var response = requestBuildings();

        // then
        assertSuccessfulListResponse(response, "data.buildings");
    }

    @Test
    @DisplayName("키워드로 캠퍼스 건물을 검색한다")
    void searchBuildings_success() {
        // given
        String keyword = "no_matching_building_keyword";

        // when
        var response = requestBuildingSearch(keyword);

        // then
        assertSuccessfulListResponse(response, "data.buildings");
    }

    @Test
    @DisplayName("카테고리로 캠퍼스 시설 목록을 조회한다")
    void getCampusPlaces_success() {
        // given
        String category = "cafe";

        // when
        var response = requestCampusPlaces(category);

        // then
        assertSuccessfulListResponse(response, "data.campusPlaces");
    }

    @Test
    @DisplayName("존재하지 않는 건물 조회 시 공통 응답을 반환한다")
    void getBuildingDetail_notFound() {
        // given
        Long nonexistentBuildingId = Long.MAX_VALUE;

        // when
        var response = requestBuildingDetail(nonexistentBuildingId);

        // then
        assertNotFoundResponse(response);
    }

    @Test
    @DisplayName("검색 키워드가 누락되면 잘못된 요청 응답을 반환한다")
    void searchBuildings_missingKeyword() {
        // when
        var response = requestBuildingSearch(null);

        // then
        assertBadRequest(response);
    }

    @Test
    @DisplayName("시설 카테고리가 누락되면 잘못된 요청 응답을 반환한다")
    void getCampusPlaces_missingCategories() {
        // when
        var response = requestCampusPlaces(null);

        // then
        assertBadRequest(response);
    }
}

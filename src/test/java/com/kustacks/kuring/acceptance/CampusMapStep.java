package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class CampusMapStep {

    private static final String BASE_URL = "/api/v2/maps";

    private CampusMapStep() {
    }

    public static ExtractableResponse<Response> requestCategories() {
        return get(BASE_URL + "/categories");
    }

    public static ExtractableResponse<Response> requestBuildings() {
        return get(BASE_URL + "/buildings");
    }

    public static ExtractableResponse<Response> requestBuildingSearch(String keyword) {
        var request = RestAssured.given().log().all();
        if (keyword != null) {
            request.queryParam("keyword", keyword);
        }

        return request
                .when().get(BASE_URL + "/buildings/search")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestCampusPlaces(String category) {
        var request = RestAssured.given().log().all();
        if (category != null) {
            request.queryParam("categories", category);
        }

        return request
                .when().get(BASE_URL + "/campus-places")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestBuildingDetail(Long buildingId) {
        return get(BASE_URL + "/buildings/" + buildingId);
    }

    public static void assertSuccessfulListResponse(
            ExtractableResponse<Response> response,
            String listPath
    ) {
        Object data = response.jsonPath().get("data");
        List<?> items = response.jsonPath().getList(listPath);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("message")).isNotBlank(),
                () -> assertThat(data).isNotNull(),
                () -> assertThat(items).isNotNull()
        );
    }

    public static void assertNotFoundResponse(ExtractableResponse<Response> response) {
        Object data = response.jsonPath().get("data");

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.jsonPath().getString("message")).isNotBlank(),
                () -> assertThat(data).isNull()
        );
    }

    public static void assertBuildingNotFoundErrorResponse(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isFalse(),
                () -> assertThat(response.jsonPath().getInt("resultCode"))
                        .isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.jsonPath().getString("resultMsg"))
                        .isEqualTo("해당 건물을 찾을 수 없습니다.")
        );
    }

    public static void assertCategoryListResponse(
            ExtractableResponse<Response> response,
            String... categoryNames
    ) {
        assertSuccessfulListResponse(response, "data.categories");

        assertThat(response.jsonPath().getList("data.categories.name", String.class))
                .containsExactly(categoryNames);
    }

    public static void assertBuildingListResponse(
            ExtractableResponse<Response> response,
            String... buildingNames
    ) {
        assertSuccessfulListResponse(response, "data.buildings");

        assertThat(response.jsonPath().getList("data.buildings.name", String.class))
                .containsExactly(buildingNames);
    }

    public static void assertBuildingListResponse(
            ExtractableResponse<Response> response,
            List<String> buildingNames,
            List<Integer> displayOrders
    ) {
        assertSuccessfulListResponse(response, "data.buildings");

        assertAll(
                () -> assertThat(response.jsonPath().getList("data.buildings.name", String.class))
                        .isEqualTo(buildingNames),
                () -> assertThat(response.jsonPath().getList("data.buildings.displayOrder", Integer.class))
                        .isEqualTo(displayOrders)
        );
    }

    public static void assertCampusPlaceListResponse(
            ExtractableResponse<Response> response,
            String placeName,
            String category,
            String buildingName,
            String imageUrl
    ) {
        assertSuccessfulListResponse(response, "data.campusPlaces");

        assertAll(
                () -> assertThat(response.jsonPath().getList("data.campusPlaces")).hasSize(1),
                () -> assertThat(response.jsonPath().getString("data.campusPlaces[0].name"))
                        .isEqualTo(placeName),
                () -> assertThat(response.jsonPath().getString("data.campusPlaces[0].category"))
                        .isEqualTo(category),
                () -> assertThat(response.jsonPath().getString("data.campusPlaces[0].imageUrl"))
                        .isEqualTo(imageUrl),
                () -> assertThat(response.jsonPath().getString("data.campusPlaces[0].building.name"))
                        .isEqualTo(buildingName)
        );
    }

    public static void assertCampusMapSearchResponse(
            ExtractableResponse<Response> response,
            String buildingName,
            String campusPlaceName
    ) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("message")).isNotBlank(),
                () -> assertThat(response.jsonPath().getList("data.buildings.name", String.class))
                        .containsExactly(buildingName),
                () -> assertThat(response.jsonPath().getList("data.campusPlaces.name", String.class))
                        .containsExactly(campusPlaceName)
        );
    }

    public static void assertBuildingDetailResponse(
            ExtractableResponse<Response> response,
            String buildingName,
            String campusPlaceName,
            String imageUrl
    ) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("data.name")).isEqualTo(buildingName),
                () -> assertThat(response.jsonPath().getString("data.imageUrl")).isEqualTo(imageUrl),
                () -> assertThat(response.jsonPath().getList("data.operatingHours")).isNotNull(),
                () -> assertThat(response.jsonPath().getList("data.campusPlaces.name", String.class))
                        .containsExactly(campusPlaceName),
                () -> assertThat(response.jsonPath().getList("data.campusPlaces.imageUrl", String.class))
                        .containsExactly(imageUrl)
        );
    }

    public static void assertBadRequest(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static ExtractableResponse<Response> get(String path) {
        return RestAssured
                .given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }
}

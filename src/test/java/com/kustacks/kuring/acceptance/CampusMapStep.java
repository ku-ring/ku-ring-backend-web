package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public final class CampusMapStep {

    private static final String BASE_URL = "/api/v1/map";

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

package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.controller.dto.SubscribeCategoriesRequestDTO;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CategoryStep {

    public static void 카테고리_조회_요청_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isEqualTo(true),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo("성공"),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getList("categories").size()).isNotZero()
        );
    }

    public static ExtractableResponse<Response> 카테고리_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/v1/notice/categories")
                .then().log().all()
                .extract();
    }

    public static void 카테고리_구독_요청_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isEqualTo(true),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo("성공"),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(201)
        );
    }

    public static ExtractableResponse<Response> 카테고리_구독_요청(SubscribeCategoriesRequestDTO reqeust) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(reqeust)
                .when().post("/api/v1/notice/subscribe")
                .then().log().all()
                .extract();
    }
}

package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.category.common.dto.SubscribeCategoriesV1Request;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CategoryStep {

    public static void 카테고리_조회_요청_응답_확인(ExtractableResponse<Response> response, String... categories) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isEqualTo(true),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo("성공"),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getList("categories")).contains(categories)
        );
    }

    public static void 카테고리_조회_요청_응답_확인_v2(ExtractableResponse<Response> response, String... categories) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("지원하는 학교 공지 카테고리 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.name")).contains(categories)
        );
    }

    public static ExtractableResponse<Response> 카테고리_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/v1/notice/categories")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리_조회_요청_v2() {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/notices/categories")
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

    public static ExtractableResponse<Response> 카테고리_구독_요청(SubscribeCategoriesV1Request reqeust) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(reqeust)
                .when().post("/api/v1/notice/subscribe")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리_수정_요청(SubscribeCategoriesV1Request request) {
        return 카테고리_구독_요청(request);
    }

    public static ExtractableResponse<Response> 사용자_카테고리_목록_조회_요청(String id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when().get("/api/v1/notice/subscribe?id={id}")
                .then().log().all()
                .extract();
    }

    public static void 학과_조회_응답_확인(ExtractableResponse<Response> response, int supportedDepartmentCnt) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("지원하는 학과 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.departmentList").size()).isEqualTo(supportedDepartmentCnt)
        );
    }

    public static ExtractableResponse<Response> 학과_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/notices/departments")
                .then().log().all()
                .extract();
    }
}

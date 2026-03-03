package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ClubStep {

    public static ExtractableResponse<Response> 지원하는_동아리_소속_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/clubs/divisions")
                .then().log().all()
                .extract();
    }

    public static void 동아리_소속_조회_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("지원하는 동아리 소속 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.divisions")).isNotEmpty()
        );
    }

    public static ExtractableResponse<Response> 동아리_목록_조회_요청(String category, String division) {
        return RestAssured
                .given().log().all()
                .queryParam("category", category)
                .queryParam("division", division)
                .when().get("/api/v2/clubs")
                .then().log().all()
                .extract();
    }

    public static void 동아리_목록_조회_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("동아리 목록 조회에 성공하였습니다"),

                () -> assertThat(response.jsonPath().getList("data.clubs")).isNotEmpty(),
                () -> assertThat(response.jsonPath().getLong("data.clubs[0].id")).isPositive(),
                () -> assertThat(response.jsonPath().getString("data.clubs[0].name")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data.clubs[0].summary")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data.clubs[0].category")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data.clubs[0].division")).isNotBlank(),
                () -> assertThat(response.jsonPath().getBoolean("data.clubs[0].isSubscribed")).isInstanceOf(Boolean.class),
                () -> assertThat(response.jsonPath().getLong("data.clubs[0].subscriberCount")).isGreaterThanOrEqualTo(0)
        );
    }

    public static ExtractableResponse<Response> 동아리_상세_조회_요청(Long clubId) {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/clubs/{id}", clubId)
                .then().log().all()
                .extract();
    }

    public static void 동아리_상세_조회_응답_확인(ExtractableResponse<Response> response, Long clubId) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("동아리 상세 조회에 성공하였습니다"),

                () -> assertThat(response.jsonPath().getLong("data.id")).isEqualTo(clubId),
                () -> assertThat(response.jsonPath().getString("data.name")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data.summary")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data.category")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data.division")).isNotBlank(),
                () -> assertThat(response.jsonPath().getLong("data.subscriberCount")).isGreaterThanOrEqualTo(0),
                () -> assertThat(response.jsonPath().getBoolean("data.isSubscribed")).isInstanceOf(Boolean.class),
                () -> assertThat(response.jsonPath().getString("data.recruitmentStatus")).isNotBlank()
        );
    }
}

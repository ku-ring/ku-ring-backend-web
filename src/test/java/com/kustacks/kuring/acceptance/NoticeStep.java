package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class NoticeStep {

    public static void 공지사항_조회_요청_응답_확인(ExtractableResponse<Response> response, String category) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isEqualTo(true),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo("성공"),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("noticeList[0].articleId")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("noticeList[0].postedDate")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("noticeList[0].subject")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("noticeList[0].category")).isEqualTo(category)
        );
    }

    public static ExtractableResponse<Response> 공지사항_조회_요청(String category) {
        return RestAssured
                .given().log().all()
                .pathParam("type", category)
                .pathParam("offset", "0")
                .pathParam("max", "10")
                .when().get("/api/v1/notice?type={type}&offset={offset}&max={max}")
                .then().log().all()
                .extract();
    }

    public static void 공지사항_조회_요청_실패_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isEqualTo(false),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo("해당 공지 카테고리를 지원하지 않습니다."),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(400)
        );
    }
}

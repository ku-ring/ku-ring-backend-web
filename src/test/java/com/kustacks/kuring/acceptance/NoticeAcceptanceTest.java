package com.kustacks.kuring.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("인수 : 공지사항")
public class NoticeAcceptanceTest extends AcceptanceTest {

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 학생 공지페이지 요청시
     * Then : 학생 공지 목록이 성공적으로 조회된다
     */
    @DisplayName("학생 공지사항을 성공적으로 조회한다")
    @Test
    public void look_up_notice() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .pathParam("type", "stu")
                .pathParam("offset", "0")
                .pathParam("max", "10")
                .when().get("/api/v1/notice?type={type}&offset={offset}&max={max}")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isEqualTo(true),
                () -> assertThat(response.jsonPath().getString("resultMsg")).isEqualTo("성공"),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("noticeList[0].articleId")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("noticeList[0].postedDate")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("noticeList[0].subject")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("noticeList[0].category")).isEqualTo("student")
        );
    }
}

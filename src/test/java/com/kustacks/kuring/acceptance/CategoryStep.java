package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoriesSubscribeRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.kustacks.kuring.support.IntegrationTestSupport.USER_FCM_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CategoryStep {

    public static ExtractableResponse<Response> 지원하는_카테고리_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/notices/categories")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 사용자가_구독한_카테고리_목록_조회_요청(String userFcmToken) {
        return RestAssured
                .given().log().all()
                .header("User-Token", userFcmToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v2/users/subscriptions/categories")
                .then().log().all()
                .extract();
    }

    public static void 카테고리_조회_요청_응답_확인(ExtractableResponse<Response> response, String... categories) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getList("data.name")).contains(categories)
        );
    }

    public static ExtractableResponse<Response> 카테고리_구독_요청(String userFcmToken, UserCategoriesSubscribeRequest reqeust) {
        return RestAssured
                .given().log().all()
                .header("User-Token", userFcmToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(reqeust)
                .when().post("/api/v2/users/subscriptions/categories")
                .then().log().all()
                .extract();
    }

    public static void 카테고리_구독_요청_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("사용자의 학교 공지 카테고리 구독에 성공하였습니다")
        );
    }

    public static ExtractableResponse<Response> 학과_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/notices/departments")
                .then().log().all()
                .extract();
    }

    public static void 학과_조회_응답_확인(ExtractableResponse<Response> response, int supportedDepartmentCnt) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("지원하는 학과 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.departmentList")).hasSize(supportedDepartmentCnt)
        );
    }

    public static ExtractableResponse<Response> 카테고리_수정_요청(UserCategoriesSubscribeRequest request) {
        return 카테고리_구독_요청(USER_FCM_TOKEN, request);
    }
}

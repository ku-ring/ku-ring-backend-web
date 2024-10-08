package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.auth.dto.UserRegisterRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserBookmarkRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoriesSubscribeRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserDepartmentsSubscribeRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserFeedbackRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UserStep {

    public static ExtractableResponse<Response> 회원_가입_요청(String token) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new UserRegisterRequest(token))
                .when().post("/api/v2/users")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리_구독_요청(String token, UserCategoriesSubscribeRequest reqeust) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", token)
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

    public static ExtractableResponse<Response> 사용자_카테고리_구독_목록_조회_요청(String id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", id)
                .when().get("/api/v2/users/subscriptions/categories")
                .then().log().all()
                .extract();
    }

    public static void 카테고리_구독_목록_조회_요청_응답_확인(ExtractableResponse<Response> response, List<String> departments) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("사용자가 구독한 학교 공지 카테고리 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.name").containsAll(departments)).isTrue()
        );
    }

    public static ExtractableResponse<Response> 학과_구독_요청(String token, List<String> departments) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", token)
                .body(new UserDepartmentsSubscribeRequest(departments))
                .when().post("/api/v2/users/subscriptions/departments")
                .then().log().all()
                .extract();
    }

    public static void 학과_구독_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("사용자의 학과 구독에 성공하였습니다")
        );
    }

    public static void 사용자_학과_조회_응답_확인(ExtractableResponse<Response> response, List<String> departments) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("사용자가 구독한 학과 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.name")).containsAll(departments)
        );
    }

    public static ExtractableResponse<Response> 구독한_학과_목록_조회_요청(String token) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", token)
                .when().get("/api/v2/users/subscriptions/departments")
                .then().log().all()
                .extract();
    }


    public static ExtractableResponse<Response> 피드백_요청_v2(String token, String feedback) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", token)
                .body(new UserFeedbackRequest(feedback))
                .when().post("/api/v2/users/feedbacks")
                .then().log().all()
                .extract();
    }

    public static void 피드백_요청_응답_확인_v2(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("피드백 저장에 성공하였습니다")
        );
    }

    public static void 북마크_응답_확인(ExtractableResponse<Response> response, HttpStatus status) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(status.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("북마크 저장에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data")).isNull()
        );
    }

    public static ExtractableResponse<Response> 북마크_생성_요청(String token, String articleId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", token)
                .body(new UserBookmarkRequest(articleId))
                .when().post("/api/v2/users/bookmarks")
                .then().log().all()
                .extract();
    }


    public static void 북마크_조회_응답_확인(ExtractableResponse<Response> 북마크_조회_응답) {
        assertAll(
                () -> assertThat(북마크_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(북마크_조회_응답.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(북마크_조회_응답.jsonPath().getString("message")).isEqualTo("북마크 조회에 성공하였습니다"),
                () -> assertThat(북마크_조회_응답.jsonPath().getList("data")).hasSize(3),
                () -> assertThat(북마크_조회_응답.jsonPath().getString("data[].articleId")).isNotBlank(),
                () -> assertThat(북마크_조회_응답.jsonPath().getString("data[].postedDate")).isNotBlank(),
                () -> assertThat(북마크_조회_응답.jsonPath().getString("data[].subject")).isNotBlank(),
                () -> assertThat(북마크_조회_응답.jsonPath().getString("data[].url")).isNotBlank(),
                () -> assertThat(북마크_조회_응답.jsonPath().getString("data[].subject")).isNotBlank()
        );
    }

    public static ExtractableResponse<Response> 북마크한_공지_조회_요청(String userToken) {
        return RestAssured
                .given().log().all()
                .header("User-Token", userToken)
                .when().get("/api/v2/users/bookmarks")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 남은_질문_횟수_조회(String userToken) {
        return RestAssured
                .given().log().all()
                .header("User-Token", userToken)
                .when().get("/api/v2/users/ask-counts")
                .then().log().all()
                .extract();
    }

    public static void 질문_횟수_응답_검증(ExtractableResponse<Response> 질문_횟수_조회_응답) {
        assertAll(
                () -> assertThat(질문_횟수_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(질문_횟수_조회_응답.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(질문_횟수_조회_응답.jsonPath().getString("message")).isEqualTo("질문 가능 횟수 조회에 성공하였습니다"),
                () -> assertThat(질문_횟수_조회_응답.jsonPath().getInt("data.leftAskCount")).isEqualTo(2),
                () -> assertThat(질문_횟수_조회_응답.jsonPath().getInt("data.maxAskCount")).isEqualTo(2)
        );
    }
}

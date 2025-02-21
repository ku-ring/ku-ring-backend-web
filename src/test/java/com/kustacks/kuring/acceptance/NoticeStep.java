package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.notice.adapter.in.web.dto.NoticeCommentCreateRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class NoticeStep {

    public static void 공지사항_조회_요청_응답_확인(ExtractableResponse<Response> response, String category) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("공지 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data[0].articleId")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].postedDate")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].url")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].subject")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].category")).isEqualTo(category),
                () -> assertThat(response.jsonPath().getString("data[0].commentCount")).isNotBlank()
        );
    }

    public static ExtractableResponse<Response> 공지사항_조회_요청(String category) {
        return 페이지_번호와_함께_공지사항_조회_요청(category, 0);
    }

    public static ExtractableResponse<Response> 페이지_번호와_함께_공지사항_조회_요청(String category, int page) {
        return RestAssured
                .given().log().all()
                .pathParam("type", category)
                .pathParam("page", String.valueOf(page))
                .pathParam("size", "10")
                .when().get("/api/v2/notices?type={type}&page={page}&size={size}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 페이지_번호와_함께_학교_공지사항_조회_요청(String category, String hostPrefix, Boolean important, int page) {
        return RestAssured
                .given().log().all()
                .pathParam("type", category)
                .pathParam("department", hostPrefix)
                .pathParam("important", important)
                .pathParam("page", String.valueOf(page))
                .pathParam("size", "10")
                .when().get("/api/v2/notices?type={type}&department={department}&important={important}&page={page}&size={size}")
                .then().log().all()
                .extract();
    }

    public static void 학교_공지_조회_응답_확인(ExtractableResponse<Response> response, Boolean important) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("공지 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getString("data[0].articleId")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].postedDate")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].url")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].subject")).isNotBlank(),
                () -> assertThat(response.jsonPath().getString("data[0].category")).isEqualTo("department"),
                () -> assertThat(response.jsonPath().getBoolean("data[0].important")).isEqualTo(important)
        );
    }

    public static void 공지사항_조회_요청_실패_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getBoolean("isSuccess")).isFalse(),
                () -> assertThat(response.jsonPath().getInt("resultCode")).isEqualTo(400)
        );
    }

    public static void 공지_조회_응답_확인(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo("공지 조회에 성공하였습니다"),
                () -> assertThat(response.jsonPath().getList("data.noticeList").size()).isNotZero()
        );
    }

    public static ExtractableResponse<Response> 공지_조회_요청(String content) {
        return RestAssured
                .given().log().all()
                .when().get("/api/v2/notices/search?content={content}", content)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 공지에_댓글_추가(long id, String userToken, String content) {
        return 공지에_댓글_추가(id, null, userToken, content);
    }

    public static ExtractableResponse<Response> 공지에_댓글_추가(long id, Long parentId, String userToken, String content) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", userToken)
                .body(new NoticeCommentCreateRequest(content, parentId))
                .when().post("/api/v2/notices/{id}/comments", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 공지에_댓글_수정(long noticeId, long commentId, String userToken, String content) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("User-Token", userToken)
                .body(new NoticeCommentCreateRequest(content, null))
                .when().post("/api/v2/notices/{id}/comments/{commentId}", noticeId, commentId)
                .then().log().all()
                .extract();
    }

    public static void 공지사항_댓글수_응답_확인(ExtractableResponse<Response> response, long id, int count) {
        assertAll(
                () -> assertThat(response.jsonPath().getLong("data[0].id")).isEqualTo(id),
                () -> assertThat(response.jsonPath().getLong("data[0].commentCount")).isEqualTo(count)
        );
    }

    public static ExtractableResponse<Response> 공지의_댓글_조회(long id, Long cursor, int size) {
        if (cursor != null) {
            return RestAssured
                    .given().log().all()
                    .queryParam("cursor", cursor)
                    .queryParam("size", size)
                    .when().get("/api/v2/notices/{id}/comments", id)
                    .then().log().all()
                    .extract();
        }

        return RestAssured
                .given().log().all()
                .queryParam("size", size)
                .when().get("/api/v2/notices/{id}/comments", id)
                .then().log().all()
                .extract();
    }

    public static void 댓글_대댓글_확인(ExtractableResponse<Response> response1, ExtractableResponse<Response> response2) {
        assertAll(
                () -> assertThat(response1.jsonPath().getList("data.comments").size()).isEqualTo(2),
                () -> assertThat(response2.jsonPath().getList("data.comments").size()).isEqualTo(2),
                () -> assertThat(response2.jsonPath().getList("data.comments[0].subComments").size()).isEqualTo(3),
                () -> assertThat(response2.jsonPath().getList("data.comments[1].subComments").size()).isZero(),
                () -> assertThat(response2.jsonPath().getList("data.endCursor")).isNull(),
                () -> assertThat(response2.jsonPath().getBoolean("data.hasNext")).isFalse()
        );
    }

    public static void 댓글_확인(ExtractableResponse<Response> response, int size, String cursor, boolean hasNext) {
        assertAll(
                () -> assertThat(response.jsonPath().getList("data.comments").size()).isEqualTo(size),
                () -> assertThat(response.jsonPath().getString("data.endCursor")).isEqualTo(cursor),
                () -> assertThat(response.jsonPath().getBoolean("data.hasNext")).isEqualTo(hasNext)
        );
    }

    public static void 댓글_삭제(String userToken, long noticeId, long commentId) {
        RestAssured
                .given().log().all()
                .header("User-Token", userToken)
                .when().delete("/api/v2/notices/{id}/comments/{commentId}", noticeId, commentId)
                .then().log().all()
                .extract();
    }
}

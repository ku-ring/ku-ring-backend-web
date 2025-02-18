package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.CategoryStep.지원하는_카테고리_조회_요청;
import static com.kustacks.kuring.acceptance.NoticeStep.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("인수 : 공지사항")
class NoticeAcceptanceTest extends IntegrationTestSupport {

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 학생 공지페이지 요청시
     * Then : 학생 공지 목록이 성공적으로 조회된다
     */
    @DisplayName("[v2] 학생 공지사항을 성공적으로 조회한다")
    @Test
    void look_up_university_notice() {
        // when
        var 공지사항_조회_요청_응답 = 공지사항_조회_요청("stu");

        // then
        공지사항_조회_요청_응답_확인(공지사항_조회_요청_응답, "student");
    }

    @DisplayName("[v2] 서버가 지원하는 학교 공지 카테고리 목록을 조회한다")
    @Test
    void look_up_support_university_category() {
        // when
        var 카테고리_조회_요청_응답 = 지원하는_카테고리_조회_요청();

        // then
        assertAll(
                () -> assertThat(카테고리_조회_요청_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(카테고리_조회_요청_응답.jsonPath().getInt("code")).isEqualTo(200),
                () -> assertThat(카테고리_조회_요청_응답.jsonPath().getList("data.name"))
                        .containsExactly("bachelor",
                                "scholarship",
                                "library",
                                "employment",
                                "national",
                                "student",
                                "industry_university",
                                "normal",
                                "department")
        );
    }

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 학생 공지페이지 요청시
     * Then : 학생 공지 목록이 성공적으로 조회된다
     */
    @DisplayName("[v2] 학교 공지사항을 성공적으로 조회한다")
    @Test
    void look_up_notice_v2() {
        // when
        var 공지사항_조회_요청_응답 = 페이지_번호와_함께_학교_공지사항_조회_요청("stu", "", Boolean.FALSE, 0);

        // then
        공지사항_조회_요청_응답_확인(공지사항_조회_요청_응답, "student");
    }

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 컴퓨터공학부 공지 요청시
     * Then : 컴퓨터공학부 공지 목록이 성공적으로 조회된다
     */
    @DisplayName("[v2] 특정 학과의 중요 공지를 조회한다.")
    @Test
    void look_up_department_important_notice_v2() {
        // when
        var 학과_공지_조회_응답 = 페이지_번호와_함께_학교_공지사항_조회_요청("dep", DepartmentName.COMPUTER.getHostPrefix(), Boolean.TRUE, 0);

        // then
        학교_공지_조회_응답_확인(학과_공지_조회_응답, Boolean.TRUE);
    }

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 컴퓨터공학부 공지 요청시
     * Then : 컴퓨터공학부 공지 목록이 성공적으로 조회된다
     */
    @DisplayName("[v2] 특정 학과의 일반 공지를 조회한다.")
    @Test
    void look_up_department_normal_notice_v2() {
        // when
        var 학과_공지_조회_응답 = 페이지_번호와_함께_학교_공지사항_조회_요청("dep", DepartmentName.COMPUTER.getHostPrefix(), Boolean.FALSE, 0);

        // then
        학교_공지_조회_응답_확인(학과_공지_조회_응답, Boolean.FALSE);
    }

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 잘못된 카테고리를 요청시
     * Then : 실패 코드를 반환한다
     */
    @DisplayName("[v2] 잘못된 카테고리를 요청한다")
    @Test
    void invalid_category_request() {
        // when
        var 공지사항_조회_요청_응답 = 공지사항_조회_요청("invalid-type");

        // then
        공지사항_조회_요청_실패_응답_확인(공지사항_조회_요청_응답);
    }

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 잘못된 페이지 넘버 요청시
     * Then : 실패 코드를 반환한다
     */
    @DisplayName("[v2] 잘못된 offset을 요청한다")
    @Test
    void invalid_offset_request() {
        // when
        var 공지사항_조회_요청_응답 = 페이지_번호와_함께_공지사항_조회_요청("student", -1);

        // then
        공지사항_조회_요청_실패_응답_확인(공지사항_조회_요청_응답);
    }

    /**
     * Given : 사전에 저장된 공지가 있다
     * When : 키워드로 검색하면
     * Then : 해당하는 공지들이 조회된다
     */
    @DisplayName("[v2] 키워드로 검색을 요청한다")
    @Test
    void search_notice_by_keyword() {
        // when
        var 공지_조회_응답 = 공지_조회_요청("subject contain student");

        // then
        공지_조회_응답_확인(공지_조회_응답);
    }

    /**
     * Given : 사전에 저장된 공지를 조회한다
     * When : 해당 공지에 댓글을 추가하면
     * Then : 성공적으로 댓글이 추가된다
     */
    @DisplayName("[v2] 공지에 댓글을 추가한다")
    @Test
    void add_comment_to_notice() {
        // given
        var 공지_조회_응답 = 공지사항_조회_요청("stu");
        var id = 공지_조회_응답.jsonPath().getLong("data[0].id");

        // when
        var response = 공지에_댓글_추가(id, USER_FCM_TOKEN, "this is comment");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given : 사전에 저장된 공지를 조회한다
     * When : 잘못된 사용자가 해당 공지에 댓글을 추가하면
     * Then : 실패 응답을 반환한다
     */
    @DisplayName("[v2] 잘못된 사용자가 공지에 댓글을 추가할 수 없다")
    @Test
    void add_comment_to_notice_by_invalid_user() {
        // given
        var 공지_조회_응답 = 공지사항_조회_요청("stu");
        var id = 공지_조회_응답.jsonPath().getLong("data[0].id");

        // when
        var response = 공지에_댓글_추가(id, "INVALID_FCM_TOKEN", "this is comment");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given : 사전에 저장된 공지를 조회한다
     * When : 해당 공지에 댓글을 추가한다
     * Then : 해당 공지 목록을 조회한다
     * Then : 성공적으로 댓글이 추가된것을 확인할 수 있다.
     */
    @DisplayName("[v2] 공지에 댓글을 추가하고 조회시 댓글 수가 정상적으로 조회된다")
    @Test
    void add_comment_to_notice_and_query() {
        // given
        var 공지_조회_응답 = 공지사항_조회_요청("stu");
        var id = 공지_조회_응답.jsonPath().getLong("data[0].id");
        공지에_댓글_추가(id, USER_FCM_TOKEN, "this is comment1");
        공지에_댓글_추가(id, USER_FCM_TOKEN, "this is comment2");

        // when
        var response = 공지사항_조회_요청("stu");

        // then
        공지사항_댓글수_응답_확인(response, id, 2);
    }
}

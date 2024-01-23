package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.CategoryStep.지원하는_카테고리_조회_요청;
import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_조회_요청_응답_확인;
import static com.kustacks.kuring.acceptance.NoticeStep.*;

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
        카테고리_조회_요청_응답_확인(카테고리_조회_요청_응답, "student", "bachelor", "employment", "department", "library");
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
     * Give : 사전에 저장된 공지가 있다
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
}

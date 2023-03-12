package com.kustacks.kuring.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.NoticeStep.공지_조회_요청;
import static com.kustacks.kuring.acceptance.NoticeStep.공지_조회_응답_확인;
import static com.kustacks.kuring.acceptance.NoticeStep.공지사항_조회_요청;
import static com.kustacks.kuring.acceptance.NoticeStep.공지사항_조회_요청_실패_응답_확인;
import static com.kustacks.kuring.acceptance.NoticeStep.공지사항_조회_요청_응답_확인;
import static com.kustacks.kuring.acceptance.NoticeStep.페이지_번호와_함께_공지사항_조회_요청;

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
        var 공지사항_조회_요청_응답 = 공지사항_조회_요청("stu");

        // then
        공지사항_조회_요청_응답_확인(공지사항_조회_요청_응답, "student");
    }

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 잘못된 카테고리를 요청시
     * Then : 실패 코드를 반환한다
     */
    @DisplayName("잘못된 카테고리를 요청한다")
    @Test
    public void invalid_category_request() {
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
    @DisplayName("잘못된 offset을 요청한다")
    @Test
    public void invalid_offset_request() {
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
    @Test
    public void search_notice_by_keyword() {
        // when
        var 공지_조회_응답 = 공지_조회_요청("subject contain student");

        // then
        공지_조회_응답_확인(공지_조회_응답);
    }
}

package com.kustacks.kuring.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.NoticeStep.공지사항_조회_요청;
import static com.kustacks.kuring.acceptance.NoticeStep.공지사항_조회_요청_응답_확인;

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
}

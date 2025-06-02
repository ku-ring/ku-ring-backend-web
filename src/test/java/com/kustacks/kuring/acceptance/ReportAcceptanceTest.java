package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.NoticeStep.공지에_댓글_추가;
import static com.kustacks.kuring.acceptance.ReportStep.신고_요청;
import static com.kustacks.kuring.acceptance.ReportStep.신고_요청_에러_응답_확인;
import static com.kustacks.kuring.acceptance.ReportStep.신고_요청_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.사용자_로그인_되어_있음;

@DisplayName("인수 : 신고")
public class ReportAcceptanceTest extends IntegrationTestSupport {

    @BeforeEach
    void 댓글_작성_되어_있음() {
        String accessToken = 사용자_로그인_되어_있음(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);
        공지에_댓글_추가(1L, accessToken, "댓글 내용 1");
    }

    @DisplayName("사용자는 댓글을 신고할 수 있다.")
    @Test
    void user_can_report_comment() {
        // when
        var 신고_요청_응답 = 신고_요청(USER_FCM_TOKEN, 1L, "COMMENT", "신고합니다!!");

        // then
        신고_요청_응답_확인(신고_요청_응답, 201, "댓글 신고에 성공했습니다");
    }

    @DisplayName("존재하지 않는 토큰을 가진 사용자는 신고할 수 없다.")
    @Test
    void user_can_not_report_comment_with_wrong_token() {
        // when
        var 신고_요청_응답 = 신고_요청("wrong token", 1L, "COMMENT", "신고합니다!!");

        // then
        신고_요청_에러_응답_확인(신고_요청_응답, 404, "해당 사용자를 찾을 수 없습니다.");
    }

    @DisplayName("존재하지 않는 신고 대상으로 신고할 수 없다.")
    @Test
    void user_can_not_report_comment_with_wrong_target_id() {
        // when
        var 신고_요청_응답 = 신고_요청(USER_FCM_TOKEN, 1L, "cumment", "신고합니다!!");

        // then
        신고_요청_에러_응답_확인(신고_요청_응답, 404, "잘못된 신고 타입입니다.");
    }

    @DisplayName("존재하지 않는 댓글 id로 신고할 수 없다.")
    @Test
    void send_email_verification_code_for_signup() {
        // when
        var 신고_요청_응답 = 신고_요청(USER_FCM_TOKEN, 999L, "COMMENT", "신고합니다!!");

        // then
        신고_요청_에러_응답_확인(신고_요청_응답, 404, "해당 댓글을 찾을 수 없습니다.");
    }
}

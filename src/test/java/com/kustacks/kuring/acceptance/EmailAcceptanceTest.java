package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;
import static com.kustacks.kuring.acceptance.EmailStep.인증_이메일_전송_요청;
import static com.kustacks.kuring.acceptance.EmailStep.인증_이메일_전송_응답_확인;
import static com.kustacks.kuring.acceptance.EmailStep.인증코드_인증_요청;
import static com.kustacks.kuring.acceptance.EmailStep.인증코드_인증_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.사용자_회원가입_요청;

@DisplayName("인수 : 이메일")
class EmailAcceptanceTest extends IntegrationTestSupport {

    public static final String NEW_EMAIL = "new-client@konkuk.ac.kr";

    @DisplayName("인증코드 이메일을 성공적으로 발송한다.")
    @Test
    void send_verification_code_email() {
        // when
        var 인증_이메일_전송_응답 = 인증_이메일_전송_요청(NEW_EMAIL);

        // then
        인증_이메일_전송_응답_확인((인증_이메일_전송_응답));
    }

    @DisplayName("인증코드를 성공적으로 인증한다.")
    @Test
    void verify_verification_code() {
        // when
        var 인증코드_인증_응답 = 인증코드_인증_요청(USER_EMAIL,"123456");

        // then
        인증코드_인증_응답_확인(인증코드_인증_응답, HttpStatus.OK);
    }

    @DisplayName("잘못된 인증코드로 인증을 시도한다.")
    @Test
    void verify_with_invalid_verification_code() {
        // when
        var 인증코드_인증_응답 = 인증코드_인증_요청(USER_EMAIL, "654321");

        // then
        실패_응답_확인(인증코드_인증_응답, HttpStatus.BAD_REQUEST);
    }


    @DisplayName("이미 가입된 이메일로 인증코드 요청을 보냄.")
    @Test
    void duplicate_email_fail() {
        //given
        사용자_회원가입_요청(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);

        // when - 이미 가입된 이메일로 인증코드 요청
        var 인증_이메일_전송_응답 = 인증_이메일_전송_요청(USER_EMAIL);

        // then
        실패_응답_확인(인증_이메일_전송_응답, HttpStatus.BAD_REQUEST);
    }
}

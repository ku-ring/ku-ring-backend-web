package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;
import static com.kustacks.kuring.acceptance.EmailStep.비밀번호초기화_인증코드_이메일_전송_요청;
import static com.kustacks.kuring.acceptance.EmailStep.비밀번호초기화_인증코드_이메일_전송_요청_둘다없음;
import static com.kustacks.kuring.acceptance.EmailStep.비밀번호초기화_인증코드_이메일_전송_요청_바디만;
import static com.kustacks.kuring.acceptance.EmailStep.비밀번호초기화_인증코드_이메일_전송_요청_토큰만;
import static com.kustacks.kuring.acceptance.EmailStep.인증_이메일_전송_응답_확인;
import static com.kustacks.kuring.acceptance.EmailStep.인증코드_인증_요청;
import static com.kustacks.kuring.acceptance.EmailStep.인증코드_인증_응답_확인;
import static com.kustacks.kuring.acceptance.EmailStep.회원가입_인증코드_이메일_전송_요청;
import static com.kustacks.kuring.acceptance.UserStep.사용자_로그인_되어_있음;

@DisplayName("인수 : 이메일")
class EmailAcceptanceTest extends IntegrationTestSupport {

    public static final String NEW_EMAIL = "new-client@konkuk.ac.kr";

    @DisplayName("회원가입 이메일 인증코드를 성공적으로 발송한다.")
    @Test
    void send_email_verification_code_for_signup() {
        // when
        var 인증_이메일_전송_응답 = 회원가입_인증코드_이메일_전송_요청(NEW_EMAIL);

        // then
        인증_이메일_전송_응답_확인((인증_이메일_전송_응답));
    }

    @DisplayName("회원가입 이메일 인증코드를 성공적으로 인증한다.")
    @Test
    void verify_verification_code() {
        // when
        var 인증코드_인증_응답 = 인증코드_인증_요청(USER_EMAIL,"123456");

        // then
        인증코드_인증_응답_확인(인증코드_인증_응답, HttpStatus.OK);
    }

    @DisplayName("비밀번호 초기화 이메일 인증코드를 성공적으로 발송한다.")
    @Test
    void send_email_verification_code_for_password_reset() {
        //given
        String accessToken = 사용자_로그인_되어_있음(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);

        // when
        var 인증_이메일_전송_응답 = 비밀번호초기화_인증코드_이메일_전송_요청(NEW_EMAIL, accessToken);

        // then
        인증_이메일_전송_응답_확인((인증_이메일_전송_응답));
    }

    @DisplayName("로그인한 사용자가 토큰으로 비밀번호 초기화 이메일 인증코드를 성공적으로 발송한다.")
    @Test
    void send_email_verification_code_for_password_reset_with_token_only() {
        //given
        String accessToken = 사용자_로그인_되어_있음(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);

        // when
        var 인증_이메일_전송_응답 = 비밀번호초기화_인증코드_이메일_전송_요청_토큰만(accessToken);

        // then
        인증_이메일_전송_응답_확인((인증_이메일_전송_응답));
    }

    @DisplayName("비로그인 사용자가 이메일로 비밀번호 초기화 인증코드를 성공적으로 발송한다.")
    @Test
    void send_email_verification_code_for_password_reset_with_body_only() {
        // when
        var 인증_이메일_전송_응답 = 비밀번호초기화_인증코드_이메일_전송_요청_바디만(USER_EMAIL);

        // then
        인증_이메일_전송_응답_확인((인증_이메일_전송_응답));
    }

    @DisplayName("토큰과 바디 둘 다 있는 경우 토큰값으로 비밀번호 초기화 인증코드를 성공적으로 발송한다.")
    @Test
    void send_email_verification_code_for_password_reset_with_both_token_and_body_fails() {
        //given
        String accessToken = 사용자_로그인_되어_있음(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);

        // when
        var 인증_이메일_전송_응답 = 비밀번호초기화_인증코드_이메일_전송_요청(USER_EMAIL, accessToken);

        // then
        인증_이메일_전송_응답_확인((인증_이메일_전송_응답));
    }

    @DisplayName("토큰과 바디 둘 다 없는 경우 비밀번호 초기화 이메일 요청은 실패한다.")
    @Test
    void send_email_verification_code_for_password_reset_with_neither_token_nor_body_fails() {
        // when
        var 인증_이메일_전송_응답 = 비밀번호초기화_인증코드_이메일_전송_요청_둘다없음();

        // then
        실패_응답_확인(인증_이메일_전송_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("잘못된 인증코드로 인증을 시도한다.")
    @Test
    void verify_with_invalid_verification_code() {
        // when
        var 인증코드_인증_응답 = 인증코드_인증_요청(USER_EMAIL, "654321");

        // then
        실패_응답_확인(인증코드_인증_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("가입된 이메일로 회원가입 인증코드 요청을 보낼 시 실패한다.")
    @Test
    void duplicate_email_fail() {
        // when - 이미 가입된 이메일로 인증코드 요청
        var 인증_이메일_전송_응답 = 회원가입_인증코드_이메일_전송_요청(USER_EMAIL);

        // then
        실패_응답_확인(인증_이메일_전송_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("미가입 이메일로 비밀번호 초기화 인증코드 요청시 실패한다.")
    @Test
    void send_password_reset_verification_code_to_unregistered_email_fails() {
        // 미가입 이메일로 요청
        var 인증_이메일_전송_응답 = 비밀번호초기화_인증코드_이메일_전송_요청_바디만("unregistered@konkuk.ac.kr");

        // then
        실패_응답_확인(인증_이메일_전송_응답, HttpStatus.NOT_FOUND);
    }

    @DisplayName("건국대 이메일(@konkuk.ac.kr)이 아닌 이메일로 회원가입 인증코드 요청시 실패한다.")
    @Test
    void send_signup_verification_code_to_non_konkuk_email_fails() {
        // 건국대 이메일이 아닌 이메일로 요청
        var 인증_이메일_전송_응답 = 회원가입_인증코드_이메일_전송_요청("test@gmail.com");

        // then
        실패_응답_확인(인증_이메일_전송_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("건국대 이메일(@konkuk.ac.kr)이 아닌 이메일로 비밀번호 초기화 인증코드 요청시 실패한다.")
    @Test
    void send_password_reset_verification_code_to_non_konkuk_email_fails() {
        // 건국대 이메일이 아닌 이메일로 요청
        var 인증_이메일_전송_응답 = 비밀번호초기화_인증코드_이메일_전송_요청_바디만("test@gmail.com");

        // then
        실패_응답_확인(인증_이메일_전송_응답, HttpStatus.BAD_REQUEST);
    }
}

package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoriesSubscribeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;
import static com.kustacks.kuring.acceptance.EmailStep.인증_이메일_전송_요청;
import static com.kustacks.kuring.acceptance.EmailStep.인증코드_인증_요청;
import static com.kustacks.kuring.acceptance.UserStep.구독한_학과_목록_조회_요청;
import static com.kustacks.kuring.acceptance.UserStep.남은_질문_횟수_조회;
import static com.kustacks.kuring.acceptance.UserStep.로그아웃_요청;
import static com.kustacks.kuring.acceptance.UserStep.로그아웃_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.로그인_요청;
import static com.kustacks.kuring.acceptance.UserStep.로그인_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.북마크_생성_요청;
import static com.kustacks.kuring.acceptance.UserStep.북마크_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.북마크_조회_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.북마크한_공지_조회_요청;
import static com.kustacks.kuring.acceptance.UserStep.사용자_로그인_되어_있음;
import static com.kustacks.kuring.acceptance.UserStep.사용자_정보_조회_요청;
import static com.kustacks.kuring.acceptance.UserStep.사용자_정보_조회_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.비밀번호_변경_요청;
import static com.kustacks.kuring.acceptance.UserStep.비밀번호_변경_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.사용자_카테고리_구독_목록_조회_요청;
import static com.kustacks.kuring.acceptance.UserStep.사용자_학과_조회_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.액세스_토큰으로_비밀번호_변경_요청;
import static com.kustacks.kuring.acceptance.UserStep.질문_횟수_응답_검증;
import static com.kustacks.kuring.acceptance.UserStep.카테고리_구독_목록_조회_요청_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.카테고리_구독_요청;
import static com.kustacks.kuring.acceptance.UserStep.카테고리_구독_요청_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.피드백_요청_v2;
import static com.kustacks.kuring.acceptance.UserStep.피드백_요청_응답_확인_v2;
import static com.kustacks.kuring.acceptance.UserStep.학과_구독_요청;
import static com.kustacks.kuring.acceptance.UserStep.학과_구독_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.회원_가입_요청;
import static com.kustacks.kuring.acceptance.UserStep.회원가입_요청;
import static com.kustacks.kuring.acceptance.UserStep.회원가입_응답_확인;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@DisplayName("인수 : 사용자")
class UserAcceptanceTest extends IntegrationTestSupport {

    public static final String NEW_EMAIL = "new-client@konkuk.ac.kr";

    /**
     * Given: 가입되지 않은 사용자가 있다
     * When: 토큰과 함께 가입 요청을 보내온다
     * Then: 성공적으로 가입한다
     */
    @DisplayName("[v2] 사용자 가입 성공")
    @Test
    void user_register_success() {
        // given
        doNothing().when(firebaseSubscribeService).subscribe(any(UserSubscribeCommand.class));

        var 회원_가입_응답 = 회원_가입_요청("test_register_token");

        // when, then
        assertThat(회원_가입_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("[v2] 사용자 가입 실패")
    @Test
    void user_register_fail() {
        // given
        doThrow(new FirebaseSubscribeException())
                .when(firebaseSubscribeService)
                .subscribe(any(UserSubscribeCommand.class));

        var 회원_가입_응답 = 회원_가입_요청("test_register_token");

        // when, then
        assertThat(회원_가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given : 구독한 카테고리가 없는 사용자가 있다
     * When : 사용자가 카테고리 구독 요청을 요청한다
     * Then : 성공 유무를 반환한다
     */
    @DisplayName("[v2] 사용자가 카테고리를 구독한다")
    @Test
    void user_subscribe_category() {
        // given
        doNothing().when(firebaseSubscribeService).subscribe(any(UserSubscribeCommand.class));

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(USER_FCM_TOKEN, new UserCategoriesSubscribeRequest(List.of("student", "employment")));

        // then
        카테고리_구독_요청_응답_확인(카테고리_구독_요청_응답);
    }

    /**
     * Given : 사용자가 사전에 구독한 카테고리들이 있다
     * When : 사용자가 구독한 카테고리 목록을 요청한다
     * Then : 구독한 카테고리 목록을 반환한다
     */
    @DisplayName("[v2] 사용자가 구독한 카테고리 목록을 조회한다")
    @Test
    void look_up_user_subscribe_category() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());
        카테고리_구독_요청(USER_FCM_TOKEN, new UserCategoriesSubscribeRequest(List.of("student", "employment")));

        // when
        var 조회_응답 = 사용자_카테고리_구독_목록_조회_요청(USER_FCM_TOKEN);

        // then
        카테고리_구독_목록_조회_요청_응답_확인(조회_응답, List.of("student", "employment"));
    }

    /**
     * Given : 구독한 학과가 없는 사용자가 있다
     * When : 사용자가 학과 구독 요청을 요청한다
     * Then : 성공 유무를 반환한다
     */
    @DisplayName("[v2] 사용자가 학과를 구독한다")
    @Test
    void user_subscribe_department() {
        // given
        doNothing().when(firebaseSubscribeService).subscribe(any(UserSubscribeCommand.class));

        // when
        var 학과_구독_응답 = 학과_구독_요청(USER_FCM_TOKEN, List.of("cse", "korea"));

        // then
        학과_구독_응답_확인(학과_구독_응답);
    }

    /**
     * Given : 사용자가 사전에 구독한 학과들이 있다
     * When : 사용자가 구독한 학과 목록을 요청한다
     * Then : 구독한 학과 목록을 반환한다
     * When : 사용자가 구독한 학과를 전부 취소한다
     * Then : 구독한 학과 목록이 비어있다
     */
    @DisplayName("[v2] 사용자가 구독한 학과 목록을 조회한다")
    @Test
    void look_up_user_subscribe_department() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());
        학과_구독_요청(USER_FCM_TOKEN, List.of("cse", "korea"));

        // when
        var 사용자_학과_조회_응답 = 구독한_학과_목록_조회_요청(USER_FCM_TOKEN);

        // then
        사용자_학과_조회_응답_확인(사용자_학과_조회_응답, List.of("computer_science", "korean"));

        // when
        학과_구독_요청(USER_FCM_TOKEN, Collections.emptyList());

        // then
        사용자_학과_조회_응답_확인(구독한_학과_목록_조회_요청(USER_FCM_TOKEN), Collections.emptyList());
    }

    /**
     * Given : 사용자가 피드백 사항을 적는다
     * When : 피드백 전송을 누르면
     * Then : 성공적으로 서버에 저장된다
     */
    @DisplayName("[v2] 사용자의 피드백을 저장한다")
    @Test
    void request_feedback() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 피드백_요청_응답 = 피드백_요청_v2(USER_FCM_TOKEN, "feedback request");

        // then
        피드백_요청_응답_확인_v2(피드백_요청_응답);
    }

    @DisplayName("[v2] 잘못된 길이의 피드백을 요청시 예외가 발생한다")
    @Test
    void request_invalid_length_feedback() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 피드백_요청_응답 = 피드백_요청_v2(USER_FCM_TOKEN, "5자미만");

        // then
        실패_응답_확인(피드백_요청_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[v2] 사용자는 원하는 공지의 북마크를 추가할 수 있다")
    @Test
    void request_bookmark() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 북마크_응답 = 북마크_생성_요청(USER_FCM_TOKEN, "article_1");

        // then
        북마크_응답_확인(북마크_응답, HttpStatus.OK);
    }

    /**
     * Given : 사용자가 사전에 저장해둔 북마크가 있다
     * When : 북마크 목록을 요청한다
     * Then : 성공적으로 북마크 목록을 반환한다
     */
    @DisplayName("[v2] 사용자는 자신이 북마크한 공지를 조회할 수 있다")
    @Test
    void lookup_bookmark() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());
        북마크_생성_요청(USER_FCM_TOKEN, "article_1");
        북마크_생성_요청(USER_FCM_TOKEN, "article_2");
        북마크_생성_요청(USER_FCM_TOKEN, "depart_normal_article_1");

        // when
        var 북마크_조회_응답 = 북마크한_공지_조회_요청(USER_FCM_TOKEN);

        // then
        북마크_조회_응답_확인(북마크_조회_응답);
    }

    /**
     * Given : 사용자가 AI 질문을 한적이 있다
     * When : 남은 질문 가능 횟수를 조회한다
     * Then : 성공적으로 질문 가능횟수와 가능한 최대 질문 횟수를 반환한다
     */
    @DisplayName("[v2] 사용자는 자신의 남은 질문 가능 횟수를 조회할 수 있다")
    @Test
    void lookup_ask_count() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 질문_횟수_조회_응답 = 남은_질문_횟수_조회(USER_FCM_TOKEN);

        // then
        질문_횟수_응답_검증(질문_횟수_조회_응답);
    }

    @DisplayName("[v2] 사용자는 이메일 인증 후 회원가입, 로그인, 로그아웃을 차례로 할 수 있다.")
    @Test
    void verify_email_and_signup() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());
        인증_이메일_전송_요청(NEW_EMAIL);
        인증코드_인증_요청(NEW_EMAIL, "123456");

        회원가입_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        var 로그인_응답 = 로그인_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);
        String jwtToken = 로그인_응답.jsonPath().getString("data.accessToken");

        // when
        var 로그아웃_응답 = 로그아웃_요청(USER_FCM_TOKEN, jwtToken);

        // then
        로그아웃_응답_확인(로그아웃_응답);
    }


    @DisplayName("[v2] 사용자는 회원가입을 할 수 있다")
    @Test
    void signup_success() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 회원가입_응답 = 회원가입_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        // then
        회원가입_응답_확인(회원가입_응답);
    }

    @DisplayName("[v2] 사용자는 로그인을 할 수 있다")
    @Test
    void login_success() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        회원가입_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        // when
        var 로그인_응답 = 로그인_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        // then
        로그인_응답_확인(로그인_응답);
    }

    @DisplayName("[v2] 사용자는 로그아웃을 할 수 있다")
    @Test
    void logout_success() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        회원가입_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        var 로그인_응답 = 로그인_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);
        String jwtToken = 로그인_응답.jsonPath().getString("data.accessToken");

        // when
        var 로그아웃_응답 = 로그아웃_요청(USER_FCM_TOKEN, jwtToken);

        // then
        로그아웃_응답_확인(로그아웃_응답);
    }

    @DisplayName("[v2] 잘못된 비밀번호로 로그인 시 실패한다")
    @Test
    void login_fail_with_wrong_password() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        회원가입_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        // when
        var 로그인_응답 = 로그인_요청(USER_FCM_TOKEN, NEW_EMAIL, "wrong_password");

        // then
        실패_응답_확인(로그인_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[v2] 존재하지 않는 이메일로 로그인 시 실패한다")
    @Test
    void login_fail_with_nonexistent_email() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());
        //회원가입 생략

        var 로그인_응답 = 로그인_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        // then
        실패_응답_확인(로그인_응답, HttpStatus.NOT_FOUND);
    }

    @DisplayName("[v2] 이미 존재하는 이메일로 회원가입 시 실패한다")
    @Test
    void signup_fail_with_duplicate_email() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // 첫번째 회원가입
        회원가입_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        // when 같은 이메일로 회원가입 요청
        var 중복_회원가입_응답 = 회원가입_요청(USER_FCM_TOKEN, NEW_EMAIL, USER_PASSWORD);

        // then
        실패_응답_확인(중복_회원가입_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[v2] 사용자는 본인의 정보를 조회할 수 있다")
    @Test
    void lookup_user_info() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        String jwtToken = 사용자_로그인_되어_있음(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);

        // when
        var 사용자_정보_조회_응답 = 사용자_정보_조회_요청(USER_FCM_TOKEN, jwtToken);

        // then
        사용자_정보_조회_응답_확인(사용자_정보_조회_응답, USER_EMAIL);
    }

    @DisplayName("[v2] 유효하지 않은 JWT 토큰으로 사용자 정보 조회 시 실패한다")
    @Test
    void lookup_user_info_with_invalid_jwt_token() {

        var 사용자_정보_조회_응답 = 사용자_정보_조회_요청(USER_FCM_TOKEN, "wrong_token");

        // then
        실패_응답_확인(사용자_정보_조회_응답, HttpStatus.UNAUTHORIZED);
        var 비밀번호_초기화_응답 = 비밀번호_변경_요청(USER_EMAIL, "new_password");

        // then
        비밀번호_변경_응답_확인(비밀번호_초기화_응답);

        // 변경된 비밀번호로 로그인이 가능한지 확인
        var 로그인_응답 = 로그인_요청(USER_FCM_TOKEN, USER_EMAIL, "new_password");
        로그인_응답_확인(로그인_응답);
    }

    @DisplayName("[v2] 사용자는 비밀번호를 변경하고 새로운 비밀번호로 로그인할 수 있다.")
    @Test
    void modify_password_with_access_token() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());
        String accessToken = 사용자_로그인_되어_있음(USER_FCM_TOKEN, USER_EMAIL, USER_PASSWORD);

        // when
        var 비밀번호_변경_응답 = 액세스_토큰으로_비밀번호_변경_요청(accessToken,"new_password");

        // then
        비밀번호_변경_응답_확인(비밀번호_변경_응답);

        // 변경된 비밀번호로 재로그인이 가능한지 확인
        로그아웃_요청(USER_FCM_TOKEN, accessToken);
        var 로그인_응답 = 로그인_요청(USER_FCM_TOKEN, USER_EMAIL, "new_password");
        로그인_응답_확인(로그인_응답);
    }

    @DisplayName("[v2] 존재하지 않는 이메일로 비밀번호 변경 시도시 실패한다")
    @Test
    void modify_password_with_wrong_email() {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 비밀번호_초기화_응답 = 비밀번호_변경_요청("wrong-email@konkuk.ac.kr", "new_password");

        // then
        실패_응답_확인(비밀번호_초기화_응답, HttpStatus.NOT_FOUND);
    }
}
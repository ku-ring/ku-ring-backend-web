package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;
import com.kustacks.kuring.message.application.port.in.dto.UserTokenValidationCommand;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoriesSubscribeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@DisplayName("인수 : 사용자")
class UserAcceptanceTest extends IntegrationTestSupport {

    /**
     * Given: 가입되지 않은 사용자가 있다
     * When: 토큰과 함께 가입 요청을 보내온다
     * Then: 성공적으로 가입한다
     */
    @DisplayName("[v2] 사용자 가입 성공")
    @Test
    void user_register_success() {
        // given
        doNothing().when(firebaseService).subscribe(any(UserSubscribeCommand.class));

        var 회원_가입_응답 = 회원_가입_요청("test_register_token");

        // when, then
        assertThat(회원_가입_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("[v2] 사용자 가입 실패")
    @Test
    void user_register_fail() {
        // given
        doThrow(new FirebaseSubscribeException())
                .when(firebaseService)
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
        doNothing().when(firebaseService).subscribe(any(UserSubscribeCommand.class));

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
        doNothing().when(firebaseService).validationToken(any(UserTokenValidationCommand.class));
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
        doNothing().when(firebaseService).subscribe(any(UserSubscribeCommand.class));

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
        doNothing().when(firebaseService).validationToken(any(UserTokenValidationCommand.class));
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
        doNothing().when(firebaseService).validationToken(any(UserTokenValidationCommand.class));

        // when
        var 피드백_요청_응답 = 피드백_요청_v2(USER_FCM_TOKEN, "feedback request");

        // then
        피드백_요청_응답_확인_v2(피드백_요청_응답);
    }

    @DisplayName("[v2] 잘못된 길이의 피드백을 요청시 예외가 발생한다")
    @Test
    void request_invalid_length_feedback() {
        // given
        doNothing().when(firebaseService).validationToken(any(UserTokenValidationCommand.class));

        // when
        var 피드백_요청_응답 = 피드백_요청_v2(USER_FCM_TOKEN, "5자미만");

        // then
        실패_응답_확인(피드백_요청_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[v2] 사용자는 원하는 공지의 북마크를 추가할 수 있다")
    @Test
    void request_bookmark() {
        // given
        doNothing().when(firebaseService).validationToken(any(UserTokenValidationCommand.class));

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
        doNothing().when(firebaseService).validationToken(any(UserTokenValidationCommand.class));
        북마크_생성_요청(USER_FCM_TOKEN, "article_1");
        북마크_생성_요청(USER_FCM_TOKEN, "article_2");
        북마크_생성_요청(USER_FCM_TOKEN, "depart_normal_article_1");

        // when
        var 북마크_조회_응답 = 북마크한_공지_조회_요청(USER_FCM_TOKEN);

        // then
        북마크_조회_응답_확인(북마크_조회_응답);
    }
}

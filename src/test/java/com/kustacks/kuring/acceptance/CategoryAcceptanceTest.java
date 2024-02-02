package com.kustacks.kuring.acceptance;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;
import com.kustacks.kuring.message.application.port.in.dto.UserTokenValidationCommand;
import com.kustacks.kuring.message.application.port.in.dto.UserUnsubscribeCommand;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoriesSubscribeRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.kustacks.kuring.acceptance.CategoryStep.*;
import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@DisplayName("인수 : 카테고리")
class CategoryAcceptanceTest extends IntegrationTestSupport {

    /**
     * Given : 쿠링앱을 실행한다
     * When : 첫 로딩화면 로딩시
     * Then : 상단에 카테고리 목록을 보여준다
     */
    @DisplayName("[v2] 서버가 지원하는 카테고리 목록을 조회한다")
    @Test
    void look_up_category_list() {
        // when
        var 카테고리_조회_요청_응답 = 지원하는_카테고리_조회_요청();

        // then
        카테고리_조회_요청_응답_확인(카테고리_조회_요청_응답, "bachelor", "scholarship", "employment", "national",
                "student", "industry_university", "normal", "library", "department");
    }


    /**
     * Given : 쿠링앱을 실행한다
     * When : 학과 목록을 요청시
     * Then : 지원하는 학과 목록을 보여준다
     */
    @DisplayName("[v2] 서버가 지원하는 학과 목록을 조회한다")
    @Test
    void look_up_department_list() {
        // when
        var 학과_조회_요청_응답 = 학과_조회_요청();

        // then
        학과_조회_응답_확인(학과_조회_요청_응답, 60);
    }

    /**
     * Given : 구독한 카테고리가 없는 사용자가 있다
     * When : 사용자가 카테고리 구독 요청을 요청한다
     * Then : 성공 유무를 반환한다
     */
    @DisplayName("[v2] 사용자가 카테고리를 구독한다")
    @Test
    void user_subscribe_category() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).subscribe(any(UserSubscribeCommand.class));

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(USER_FCM_TOKEN, new UserCategoriesSubscribeRequest(List.of("student", "employment")));

        // then
        카테고리_구독_요청_응답_확인(카테고리_구독_요청_응답);
    }

    /**
     * Given : 구독한 카테고리가 없는 사용자가 있다
     * When : 사용자가 비정상 토큰과 함께 카테고리 구독을 요청한다
     * Then : 실패코드를 반환한다
     */
    @DisplayName("[v2] 사용자가 잘못된 토큰과 함께 카테고리 구독시 실패한다")
    @Test
    @Disabled
    void user_subscribe_category_with_invalid_token() {
        // given
        doThrow(new FirebaseInvalidTokenException())
                .when(firebaseService)
                .validationToken(any(UserTokenValidationCommand.class));

        // when
        var response = 카테고리_구독_요청("user_invalid_token", new UserCategoriesSubscribeRequest(List.of("student", "employment")));

        // then
        실패_응답_확인(response, HttpStatus.UNAUTHORIZED);
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
        var 조회_응답 = 사용자가_구독한_카테고리_목록_조회_요청(USER_FCM_TOKEN);

        // then
        카테고리_조회_요청_응답_확인(조회_응답, "student", "employment");
    }

    /**
     * Given : 사용자가 사전에 구독한 카테고리들이 있다
     * When : 카테고리 수정을 요청한다
     * Then : 성공적으로 수정된다
     * When : 사용자가 구독한 카테고리 목록을 요청한다
     * Then : 구독한 카테고리 목록을 반환한다
     */
    @DisplayName("[v2] 사용자가 구독한 카테고리 목록을 수정한다")
    @Test
    void edit_user_subscribe_category() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).validationToken(any(UserTokenValidationCommand.class));
        doNothing().when(firebaseService).subscribe(any(UserSubscribeCommand.class));
        doNothing().when(firebaseService).unsubscribe(any(UserUnsubscribeCommand.class));

        카테고리_구독_요청(USER_FCM_TOKEN, new UserCategoriesSubscribeRequest(List.of("student", "employment")));

        // when
        var 카테고리_구독_요청_응답 = 카테고리_수정_요청(new UserCategoriesSubscribeRequest(List.of("student", "library")));

        // then
        카테고리_구독_요청_응답_확인(카테고리_구독_요청_응답);

        // when
        var 조회_응답 = 사용자가_구독한_카테고리_목록_조회_요청(USER_FCM_TOKEN);

        // then
        카테고리_조회_요청_응답_확인(조회_응답, "student", "library");
    }

    @DisplayName("[v2] 요청 JSON body 필드 누락시 예외 발생")
    @Test
    void json_body_miss() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).subscribe(any(UserSubscribeCommand.class));

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(USER_FCM_TOKEN, new UserCategoriesSubscribeRequest(null));

        // then
        실패_응답_확인(카테고리_구독_요청_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[v2] 잘못된 카테고리 구독 요청시 예외 발생")
    @Test
    void user_subscribe_invalid_category() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).subscribe(any(UserSubscribeCommand.class));

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(USER_FCM_TOKEN, new UserCategoriesSubscribeRequest(List.of("invalid-category")));

        // then
        실패_응답_확인(카테고리_구독_요청_응답, HttpStatus.BAD_REQUEST);
    }
}

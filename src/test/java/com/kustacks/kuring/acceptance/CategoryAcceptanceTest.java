package com.kustacks.kuring.acceptance;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.category.common.dto.request.SubscribeCategoriesRequest;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.firebase.FirebaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.kustacks.kuring.acceptance.CategoryStep.사용자_카테고리_목록_조회_요청;
import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_구독_요청;
import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_구독_요청_응답_확인;
import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_수정_요청;
import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_조회_요청;
import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_조회_요청_응답_확인;
import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@DisplayName("인수 : 카테고리")
public class CategoryAcceptanceTest extends AcceptanceTest {

    @MockBean
    FirebaseService firebaseService;

    /**
     * Given : 쿠링앱을 실행한다
     * When : 첫 로딩화면 로딩시
     * Then : 상단에 카테고리 목록을 보여준다
     */
    @DisplayName("서버가 지원하는 카테고리 목록을 조회한다")
    @Test
    public void look_up_category_list() {
        // when
        var 카테고리_조회_요청_응답 = 카테고리_조회_요청();

        // then
        카테고리_조회_요청_응답_확인(카테고리_조회_요청_응답, "student", "bachelor", "employment");
    }

    /**
     * Given : 구독한 카테고리가 없는 사용자가 있다
     * When : 사용자가 카테고리 구독 요청을 요청한다
     * Then : 성공 유무를 반환한다
     */
    @DisplayName("사용자가 카테고리를 구독한다")
    @Test
    public void user_subscribe_category() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).subscribe(anyString(), anyString());

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(new SubscribeCategoriesRequest(USER_FCM_TOKEN, List.of("student", "employment")));

        // then
        카테고리_구독_요청_응답_확인(카테고리_구독_요청_응답);
    }

    /**
     * Given : 구독한 카테고리가 없는 사용자가 있다
     * When : 사용자가 비정상 토큰과 함께 카테고리 구독을 요청한다
     * Then : 실패코드를 반환한다
     */
    @DisplayName("사용자가 잘못된 토큰과 함께 카테고리 구독시 실패한다")
    @Test
    public void user_subscribe_category_with_invalid_token() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).subscribe(anyString(), anyString());
        doThrow(new APIException(ErrorCode.API_FB_INVALID_TOKEN)).when(firebaseService).validationToken(anyString());

        // when
        var response = 카테고리_구독_요청(new SubscribeCategoriesRequest(INVALID_USER_FCM_TOKEN, List.of("student", "employment")));

        // then
        실패_응답_확인(response, 401);
    }

    /**
     * Given : 사용자가 사전에 구독한 카테고리들이 있다
     * When : 사용자가 구독한 카테고리 목록을 요청한다
     * Then : 구독한 카테고리 목록을 반환한다
     */
    @DisplayName("사용자가 구독한 카테고리 목록을 조회한다")
    @Test
    public void look_up_user_subscribe_category() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).verifyToken(anyString());
        카테고리_구독_요청(new SubscribeCategoriesRequest(USER_FCM_TOKEN, List.of("student", "employment")));

        // when
        var 조회_응답 = 사용자_카테고리_목록_조회_요청(USER_FCM_TOKEN);

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
    @DisplayName("사용자가 구독한 카테고리 목록을 수정한다")
    @Test
    public void edit_user_subscribe_category() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).verifyToken(anyString());
        doNothing().when(firebaseService).subscribe(anyString(), anyString());
        doNothing().when(firebaseService).unsubscribe(anyString(), anyString());

        카테고리_구독_요청(new SubscribeCategoriesRequest(USER_FCM_TOKEN, List.of("student", "employment")));

        // when
        var 카테고리_구독_요청_응답 = 카테고리_수정_요청(new SubscribeCategoriesRequest(USER_FCM_TOKEN, List.of("student", "library")));

        // then
        카테고리_구독_요청_응답_확인(카테고리_구독_요청_응답);

        // when
        var 조회_응답 = 사용자_카테고리_목록_조회_요청(USER_FCM_TOKEN);

        // then
        카테고리_조회_요청_응답_확인(조회_응답, "student", "library");
    }

    @DisplayName("요청 JSON body 필드 누락시 예외 발생")
    @Test
    public void json_body_miss() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).subscribe(anyString(), anyString());

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(new SubscribeCategoriesRequest(null, List.of("student", "employment")));

        // then
        실패_응답_확인(카테고리_구독_요청_응답, 400);
    }

    @DisplayName("잘못된 카테고리 구독 요청시 예외 발생")
    @Test
    public void user_subscribe_invalid_category() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseService).subscribe(anyString(), anyString());

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(new SubscribeCategoriesRequest(null, List.of("invalid-category")));

        // then
        실패_응답_확인(카테고리_구독_요청_응답, 400);
    }
}

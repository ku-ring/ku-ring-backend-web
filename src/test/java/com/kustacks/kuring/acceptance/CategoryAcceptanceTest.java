package com.kustacks.kuring.acceptance;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.SubscribeCategoriesRequestDTO;
import com.kustacks.kuring.service.FirebaseService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

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
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청(new SubscribeCategoriesRequestDTO(USER_FCM_TOKEN, List.of("student", "employment")));

        // then
        카테고리_구독_요청_응답_확인(카테고리_구독_요청_응답);
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
        카테고리_구독_요청(new SubscribeCategoriesRequestDTO(USER_FCM_TOKEN, List.of("student", "employment")));

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

        카테고리_구독_요청(new SubscribeCategoriesRequestDTO(USER_FCM_TOKEN, List.of("student", "employment")));

        // when
        var 카테고리_구독_요청_응답 = 카테고리_수정_요청(new SubscribeCategoriesRequestDTO(USER_FCM_TOKEN, List.of("student", "library")));

        // then
        카테고리_구독_요청_응답_확인(카테고리_구독_요청_응답);

        // when
        var 조회_응답 = 사용자_카테고리_목록_조회_요청(USER_FCM_TOKEN);

        // then
        카테고리_조회_요청_응답_확인(조회_응답, "student", "library");
    }
}

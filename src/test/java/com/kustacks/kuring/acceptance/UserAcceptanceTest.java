package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.user.common.SubscribeCategoriesRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.kustacks.kuring.acceptance.UserStep.구독한_학과_목록_조회_요청;
import static com.kustacks.kuring.acceptance.UserStep.사용자_카테고리_구독_목록_조회_요청_v2;
import static com.kustacks.kuring.acceptance.UserStep.사용자_학과_조회_응답_확인;
import static com.kustacks.kuring.acceptance.UserStep.카테고리_구독_요청_v2;
import static com.kustacks.kuring.acceptance.UserStep.카테고리_구독_요청_응답_확인_v2;
import static com.kustacks.kuring.acceptance.UserStep.카테고리_구독_목록_조회_요청_응답_확인_v2;
import static com.kustacks.kuring.acceptance.UserStep.학과_구독_요청;
import static com.kustacks.kuring.acceptance.UserStep.학과_구독_응답_확인;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@DisplayName("인수 : 사용자")
class UserAcceptanceTest extends AcceptanceTest {

    @MockBean
    FirebaseService firebaseService;

    /**
     * Given : 구독한 카테고리가 없는 사용자가 있다
     * When : 사용자가 카테고리 구독 요청을 요청한다
     * Then : 성공 유무를 반환한다
     */
    @DisplayName("[v2] 사용자가 카테고리를 구독한다")
    @Test
    void user_subscribe_category() {
        // given
        doNothing().when(firebaseService).subscribe(anyString(), anyString());

        // when
        var 카테고리_구독_요청_응답 = 카테고리_구독_요청_v2(USER_FCM_TOKEN, new SubscribeCategoriesRequest(List.of("student", "employment")));

        // then
        카테고리_구독_요청_응답_확인_v2(카테고리_구독_요청_응답);
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
        doNothing().when(firebaseService).validationToken(anyString());
        카테고리_구독_요청_v2(USER_FCM_TOKEN, new SubscribeCategoriesRequest(List.of("student", "employment")));

        // when
        var 조회_응답 = 사용자_카테고리_구독_목록_조회_요청_v2(USER_FCM_TOKEN);

        // then
        카테고리_구독_목록_조회_요청_응답_확인_v2(조회_응답, "student", "employment");
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
        doNothing().when(firebaseService).subscribe(anyString(), anyString());

        // when
        var 학과_구독_응답 = 학과_구독_요청(USER_FCM_TOKEN, List.of("cse", "korea"));

        // then
        학과_구독_응답_확인(학과_구독_응답);
    }

    /**
     * Given : 사용자가 사전에 구독한 학과들이 있다
     * When : 사용자가 구독한 학과 목록을 요청한다
     * Then : 구독한 학과 목록을 반환한다
     */
    @DisplayName("[v2] 사용자가 구독한 학과 목록을 조회한다")
    @Test
    void look_up_user_subscribe_department() {
        // given
        doNothing().when(firebaseService).validationToken(anyString());
        학과_구독_요청(USER_FCM_TOKEN, List.of("cse", "korea"));

        // when
        var 사용자_학과_조회_응답 = 구독한_학과_목록_조회_요청(USER_FCM_TOKEN);

        // then
        사용자_학과_조회_응답_확인(사용자_학과_조회_응답);
    }
}

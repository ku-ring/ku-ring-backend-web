package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.common.firebase.FirebaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.kustacks.kuring.acceptance.UserStep.학과_구독_요청;
import static com.kustacks.kuring.acceptance.UserStep.학과_구독_응답_확인;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@DisplayName("인수 : 사용자")
public class UserAcceptanceTest extends AcceptanceTest {

    @MockBean
    FirebaseService firebaseService;

    /**
     * Given : 구독한 학과가 없는 사용자가 있다
     * When : 사용자가 학과 구독 요청을 요청한다
     * Then : 성공 유무를 반환한다
     */
    @DisplayName("사용자가 학과를 구독한다")
    @Test
    void user_subscribe_department() {
        // given
        doNothing().when(firebaseService).subscribe(anyString(), anyString());

        // when
        var 학과_구독_응답 = 학과_구독_요청(USER_FCM_TOKEN, List.of("cse", "korea"));

        // then
        학과_구독_응답_확인(학과_구독_응답);
    }
}

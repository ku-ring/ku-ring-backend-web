package com.kustacks.kuring.acceptance;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;
import static com.kustacks.kuring.acceptance.FeedbackStep.피드백_요청;
import static com.kustacks.kuring.acceptance.FeedbackStep.피드백_요청_응답_확인;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@DisplayName("인수: 피드백")
class FeedbackAcceptanceTest extends IntegrationTestSupport {

    /**
     * Given : 사용자가 피드백 사항을 적는다
     * When : 피드백 전송을 누르면
     * Then : 성공적으로 서버에 저장된다
     */
    @DisplayName("[v2] 사용자의 피드백을 저장한다")
    @Test
    public void request_feedback() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 피드백_요청_응답 = 피드백_요청(USER_FCM_TOKEN, "feedback request");

        // then
        피드백_요청_응답_확인(피드백_요청_응답);
    }

    @DisplayName("[v2] 잘못된 길이의 피드백을 요청시 예외가 발생한다")
    @Test
    public void request_invalid_length_feedback() throws FirebaseMessagingException {
        // given
        doNothing().when(firebaseSubscribeService).validationToken(anyString());

        // when
        var 피드백_요청_응답 = 피드백_요청(USER_FCM_TOKEN, "");

        // then
        실패_응답_확인(피드백_요청_응답, HttpStatus.BAD_REQUEST);
    }
}

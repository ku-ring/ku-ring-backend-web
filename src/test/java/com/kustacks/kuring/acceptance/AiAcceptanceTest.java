package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.kustacks.kuring.acceptance.AiStep.모델_응답_확인;
import static com.kustacks.kuring.acceptance.AiStep.사용자_질문_요청;

@DisplayName("인수 : 인공지능")
class AiAcceptanceTest extends IntegrationTestSupport {

    WebTestClient client;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    /**
     * Given : 쿠링앱이 실행중이다
     * When : 교내,외 장학금 및 학자금 대출 관련 전화번호들을 묻는다
     * Then : 해당 관련 전화번호들을 반환한다
     */
    @DisplayName("[v2] 사용자가 궁금한 학교 정보를 물어볼 수 있다")
    @Test
    void ask_to_open_ai() {
        // given
        String question = "교내,외 장학금 및 학자금 대출 관련 전화번호들을 안내를 해줘";

        // when
        var 모델_응답 = 사용자_질문_요청(client, question, USER_FCM_TOKEN);

        // then
        모델_응답_확인(모델_응답, "학", "생", "복", "지", "처", "장", "학", "복", "지", "팀", "의",
                "전", "화", "번", "호", "는", "0", "2", "-", "4", "5", "0", "-", "3", "2", "1",
                "1", "~", "2", "이", "며", ",", "건", "국", "사", "랑", "/", "장", "학", "사", "정",
                "관", "장", "학", "/", "기", "금", "장", "학", "과", "관", "련", "된", "문", "의",
                "는", "0", "2", "-", "4", "5", "0", "-", "3", "9", "6", "7", "로", "하", "시",
                "면", "됩", "니", "다", ".");
    }

    /**
     * Given : 가능한 질문 횟수를 모두 사용하였다
     * When : AI에게 질문을 한다
     * Then : 질문 토큰이 부족하다는 메시지를 반환한다
     */
    @DisplayName("[v2] 가능한 질문 횟수를 모두 사용한 경우 AI에게 질문을 할 수 없다")
    @Test
    void ask_to_open_ai_overflow_count() {
        // given
        String question = "교내,외 장학금 및 학자금 대출 관련 전화번호들을 안내를 해줘";
        사용자_질문_요청(client, question, USER_FCM_TOKEN);
        사용자_질문_요청(client, question, USER_FCM_TOKEN);

        // when
        var 모델_응답 = 사용자_질문_요청(client, question, USER_FCM_TOKEN);

        // then
        모델_응답_확인(모델_응답, "남", "은", "질", "문", "횟", "수", "가", "부", "족", "합", "니", "다", ".");
    }

    /**
     * Given : 사용자의 질문과 유사한 정보가 없다
     * When : AI에게 질문을 한다
     * Then : 질문 토큰이 부족하다는 메시지를 반환한다
     */
    @DisplayName("[v2] 유사한 정보가 없는 경우 AI에게 질문을 할 수 없다")
    @Test
    void ask_to_open_ai_no_info() {
        // given
        String question = "잘못된 질문";

        // when
        var 모델_응답 = 사용자_질문_요청(client, question, USER_FCM_TOKEN);

        // then : 죄송합니다, 해당 내용은 2024년도 6월 이후에 작성된 공지 내용에서 확인할 수 없는 내용입니다.
        모델_응답_확인(모델_응답, "죄", "송", "합", "니", "다", ",", "해", "당", "내", "용", "은", "2", "0", "2", "4",
                "년", "도", "6", "월", "이", "후", "에", "작", "성", "된", "공", "지", "내", "용", "에", "서", "확", "인", "할",
                "수", "없", "는", "내", "용", "입", "니", "다", ".");
    }
}


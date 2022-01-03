package com.kustacks.kuring.kuapi.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisLoginRequestBody;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringJUnitConfig({RenewSessionKuisAuthManager.class, KuisLoginRequestBody.class})
@TestPropertySource("classpath:constants.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class RenewSessionKuisAuthManagerTest {

    @Value("${auth.login-url}")
    private String loginUrl;

    @SpyBean
    RestTemplate restTemplate;

    private final KuisAuthManager renewSessionKuisAuthManager;
    private MockRestServiceServer server;

    public RenewSessionKuisAuthManagerTest(KuisAuthManager renewSessionKuisAuthManager, RestTemplate restTemplate) {

        this.renewSessionKuisAuthManager = renewSessionKuisAuthManager;
        this.restTemplate = restTemplate;
    }

    @AfterEach
    void setUpAfter() {
        renewSessionKuisAuthManager.forceRenewing();
    }

    @BeforeEach
    void setUpBefore() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Nested
    @DisplayName("세션 갱신 - 일반 시나리오")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class NormalSenario {

        @Test
        @Order(1)
        @DisplayName("성공")
        void success() {
    
            // given
            String successResponseBody = "success";
            String TEST_COOKIE = "JSESSIONID=TEST_SESSION_ID";
            HttpHeaders responseHttpHeaders = new HttpHeaders();
            List<String> cookies = new LinkedList<>();
            cookies.add(TEST_COOKIE);
            responseHttpHeaders.put("Set-Cookie", cookies);

            server.expect(requestTo(loginUrl)).andRespond(withSuccess().headers(responseHttpHeaders).body(successResponseBody));
    
            // when
            String sessionId = renewSessionKuisAuthManager.getSessionId();
    
            // then
            assertEquals(TEST_COOKIE, sessionId);
        }

        @Test
        @Order(2)
        @DisplayName("실패 - 응답 body가 없음")
        void failByNoBody() {

            // given
            server.expect(requestTo(loginUrl)).andRespond(withSuccess());

            // when
            InternalLogicException e = assertThrows(InternalLogicException.class, renewSessionKuisAuthManager::getSessionId);

            // then
            assertEquals(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY, e.getErrorCode());
        }

        @Test
        @Order(3)
        @DisplayName("실패 - 응답 body에 success 문자열이 없음 (잘못된 아이디 혹은 비밀번호)")
        void failByNoSuccessStringInBody() {

            // given
            String badResponseBody = "fail";
            server.expect(requestTo(loginUrl)).andRespond(withSuccess().body(badResponseBody));

            // when
            InternalLogicException e = assertThrows(InternalLogicException.class, renewSessionKuisAuthManager::getSessionId);

            // then
            assertEquals(ErrorCode.KU_LOGIN_BAD_RESPONSE, e.getErrorCode());
        }

        @Test
        @Order(4)
        @DisplayName("실패 - Set-Cookie 헤더가 없음")
        void failByNoSetCookieHeader() {

            // given
            String badResponseBody = "success";
            server.expect(requestTo(loginUrl)).andRespond(withSuccess().body(badResponseBody));

            // when
            InternalLogicException e = assertThrows(InternalLogicException.class, renewSessionKuisAuthManager::getSessionId);

            // then
            assertEquals(ErrorCode.KU_LOGIN_NO_COOKIE_HEADER, e.getErrorCode());
        }

        @Test
        @Order(5)
        @DisplayName("실패 - Set-Cookie 헤더에 JSESSIONID 쿠키가 없음")
        void failByNoJsessionId() {

            // given
            String successResponseBody = "success";
            String TEST_COOKIE = "WIRED_COOKIE=FAIL_TEST";
            HttpHeaders responseHttpHeaders = new HttpHeaders();
            List<String> cookies = new LinkedList<>();
            cookies.add(TEST_COOKIE);
            responseHttpHeaders.put("Set-Cookie", cookies);

            server.expect(requestTo(loginUrl)).andRespond(withSuccess().headers(responseHttpHeaders).body(successResponseBody));

            // when
            InternalLogicException e = assertThrows(InternalLogicException.class, renewSessionKuisAuthManager::getSessionId);

            // then
            assertEquals(ErrorCode.KU_LOGIN_NO_JSESSION, e.getErrorCode());
        }
    }
}

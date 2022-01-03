package com.kustacks.kuring.kuapi.notice;

import com.kustacks.kuring.config.RetryConfig;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisLoginRequestBody;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;

@SpringJUnitConfig(classes = {RenewSessionKuisAuthManager.class, KuisLoginRequestBody.class, RetryConfig.class})
@TestPropertySource("classpath:constants.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class RenewSessionKuisAuthManagerRetryTest {

    @Value("${auth.login-url}")
    private String loginUrl;

    @SpyBean
    RestTemplate restTemplate;

    private final KuisAuthManager renewSessionKuisAuthManager;
    private MockRestServiceServer server;

    public RenewSessionKuisAuthManagerRetryTest(
            KuisAuthManager renewSessionKuisAuthManager,
//            KuisLoginRequestBody kuisLoginRequestBody,
            RestTemplate restTemplate
    ) {

        this.renewSessionKuisAuthManager = renewSessionKuisAuthManager;
//        this.kuisLoginRequestBody = kuisLoginRequestBody;
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

    @Test
    @DisplayName("실패")
    @Order(1)
    void failAfterRetry() {

        // given
        server.expect(times(3), requestTo(loginUrl)).andRespond(withException(new IOException()));

        // when, then
        InternalLogicException e = assertThrows(InternalLogicException.class, renewSessionKuisAuthManager::getSessionId);
        assertEquals(ErrorCode.KU_LOGIN_BAD_RESPONSE, e.getErrorCode());
    }
}

package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.config.JsonConfig;
import com.kustacks.kuring.config.RetryConfig;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.notice.RenewSessionKuisAuthManager;
import com.kustacks.kuring.kuapi.notice.dto.request.*;
import com.kustacks.kuring.util.converter.KuisNoticeDTOToCommonFormatDTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringJUnitConfig({KuisNoticeAPIClient.class, RestTemplate.class, KuisNoticeDTOToCommonFormatDTOConverter.class,
        BachelorKuisNoticeRequestBody.class,
        ScholarshipKuisNoticeRequestBody.class,
        EmploymentKuisNoticeRequestBody.class,
        NationalKuisNoticeRequestBody.class,
        StudentKuisNoticeRequestBody.class,
        IndustryUnivKuisNoticeRequestBody.class,
        NormalKuisNoticeRequestBody.class,
        JsonConfig.class, RetryConfig.class})
@TestPropertySource("classpath:constants.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class KuisNoticeAPIClientRetryTest {

    private final NoticeAPIClient kuisNoticeAPIClient;
    private final RestTemplate restTemplate;

    @MockBean
    private RenewSessionKuisAuthManager renewSessionKuisAuthManager;

    private MockRestServiceServer server;

    public KuisNoticeAPIClientRetryTest(NoticeAPIClient kuisNoticeAPIClient, RestTemplate restTemplate) {

        this.kuisNoticeAPIClient = kuisNoticeAPIClient;
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("실패 - 로그인 세션 획득 3회 실패")
    void failAfterRetry() {

        // given
        given(renewSessionKuisAuthManager.getSessionId()).willThrow(new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE, new RestClientException("로그인 세션 획득 실패")));

        // when, then
        InternalLogicException e = assertThrows(InternalLogicException.class, () -> kuisNoticeAPIClient.getNotices(CategoryName.BACHELOR));
        assertEquals(ErrorCode.KU_LOGIN_BAD_RESPONSE, e.getErrorCode());
    }
}

package com.kustacks.kuring.worker.client.notice;

import com.kustacks.kuring.config.JsonConfig;
import com.kustacks.kuring.config.RetryConfig;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.common.utils.converter.KuisNoticeDtoToCommonFormatDtoConverter;
import com.kustacks.kuring.worker.scrap.client.auth.KuisAuthManager;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.update.notice.dto.request.BachelorKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.EmploymentKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.IndustryUnivKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.NationalKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.NormalKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.ScholarshipKuisNoticeRequestBody;
import com.kustacks.kuring.worker.update.notice.dto.request.StudentKuisNoticeRequestBody;
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

@SpringJUnitConfig({KuisNoticeApiClient.class, RestTemplate.class, KuisNoticeDtoToCommonFormatDtoConverter.class,
        BachelorKuisNoticeRequestBody.class,
        ScholarshipKuisNoticeRequestBody.class,
        EmploymentKuisNoticeRequestBody.class,
        NationalKuisNoticeRequestBody.class,
        StudentKuisNoticeRequestBody.class,
        IndustryUnivKuisNoticeRequestBody.class,
        NormalKuisNoticeRequestBody.class,
        JsonConfig.class, RetryConfig.class})
@TestPropertySource("classpath:test-constants.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class KuisNoticeApiClientRetryTest {

    private final NoticeApiClient kuisNoticeAPIClient;
    private final RestTemplate restTemplate;

    @MockBean
    private KuisAuthManager kuisAuthManager;

    private MockRestServiceServer server;

    public KuisNoticeApiClientRetryTest(NoticeApiClient kuisNoticeAPIClient, RestTemplate restTemplate) {

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
        given(kuisAuthManager.getSessionId()).willThrow(new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE, new RestClientException("로그인 세션 획득 실패")));

        // when, then
        InternalLogicException e = assertThrows(InternalLogicException.class, () -> kuisNoticeAPIClient.request(CategoryName.BACHELOR));
        assertEquals(ErrorCode.KU_LOGIN_BAD_RESPONSE, e.getErrorCode());
    }
}

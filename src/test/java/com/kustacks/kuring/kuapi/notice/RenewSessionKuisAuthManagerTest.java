package com.kustacks.kuring.kuapi.notice;

import com.kustacks.kuring.config.JsonConfig;
import com.kustacks.kuring.config.RestConfig;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisLoginRequestBody;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringJUnitConfig({
    RenewSessionKuisAuthManager.class, KuisLoginRequestBody.class,
    RestConfig.class, JsonConfig.class
})
@TestPropertySource(locations = "classpath:test-constants.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class RenewSessionKuisAuthManagerTest {

    @Value("${auth.login-url}")
    private String loginUrl;

    @Value("${auth.api-skeleton-producer-url}")
    private String apiSkeletonProducerUrl;

    @Value("${auth.session}")
    private String testCookie;

    RestTemplate restTemplate;

    private final KuisAuthManager renewSessionKuisAuthManager;
    private final String testApiSkeleton;
    private final String successResponseBody = "{\"META\": \"success\"}";
    private final String failResponseBody = "{\"ERRCODE\": \"허용되지 않은 접근입니다.\"}";
    private MockRestServiceServer server;

    public RenewSessionKuisAuthManagerTest(
            KuisAuthManager renewSessionKuisAuthManager,
            RestTemplate restTemplate,
            @Value("${auth.api-skeleton-file-path}") String apiSkeletonFilePath) throws IOException {

        this.renewSessionKuisAuthManager = renewSessionKuisAuthManager;
        this.restTemplate = restTemplate;

        testApiSkeleton = readApiSkeleton(apiSkeletonFilePath);
    }

    @AfterEach
    void setUpAfter() {
        renewSessionKuisAuthManager.forceRenewing();
    }

    @BeforeEach
    void setUpBefore() {
        server = MockRestServiceServer.createServer(restTemplate);
        renewSessionKuisAuthManager.forceRenewing();
    }

    @Test
    @Order(1)
    @DisplayName("성공")
    void success() {

        // given
        server.expect(requestTo(apiSkeletonProducerUrl)).andRespond(withSuccess().body(testApiSkeleton));
        server.expect(requestTo(loginUrl)).andExpect(method(HttpMethod.POST)).andRespond(withSuccess().body(successResponseBody));

        // when
        String sessionId = renewSessionKuisAuthManager.getSessionId();

        // then
        assertEquals(testCookie, sessionId);
    }

//    @Test
//    @Order(2)
//    @DisplayName("실패 - 응답 body가 없음")
//    void failByNoBody() {
//
//        // given
//        server.expect(requestTo(apiSkeletonProducerUrl)).andRespond(withSuccess().body(testApiSkeleton));
//        server.expect(requestTo(loginUrl)).andExpect(method(HttpMethod.POST)).andRespond(withSuccess());
//
//        // when
//        InternalLogicException e = assertThrows(InternalLogicException.class, renewSessionKuisAuthManager::getSessionId);
//
//        // then
//        assertEquals(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY, e.getErrorCode());
//    }
//
//    @Test
//    @Order(3)
//    @DisplayName("실패 - 응답 body에 success 문자열이 없음 (kuis 로그인 방식이 바뀜 or api skeleton 최신화 안됨)")
//    void failByNoSuccessStringInBody() {
//
//        // given
//        server.expect(requestTo(apiSkeletonProducerUrl)).andRespond(withSuccess().body(testApiSkeleton));
//        server.expect(requestTo(loginUrl)).andExpect(method(HttpMethod.POST)).andRespond(withSuccess().body(failResponseBody));
//
//        // when
//        InternalLogicException e = assertThrows(InternalLogicException.class, renewSessionKuisAuthManager::getSessionId);
//
//        // then
//        assertEquals(ErrorCode.KU_LOGIN_BAD_RESPONSE, e.getErrorCode());
//    }

    private String readApiSkeleton(String path) throws IOException {
        System.out.println(path);
        ClassPathResource resource = new ClassPathResource(path);
        return resourceToString(resource.getInputStream());
    }

    private String resourceToString(InputStream inputStream) throws IOException {
        return FileCopyUtils.copyToString(new InputStreamReader(inputStream));
    }
}

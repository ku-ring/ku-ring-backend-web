package com.kustacks.kuring.worker.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.utils.encoder.RequestBodyEncoder;
import com.kustacks.kuring.config.JsonConfig;
import com.kustacks.kuring.config.RestTemplateConfig;
import com.kustacks.kuring.worker.scrap.client.auth.KuisAuthManager;
import com.kustacks.kuring.worker.scrap.client.auth.ParsingKuisAuthManager;
import com.kustacks.kuring.worker.scrap.client.auth.property.ParsingKuisAuthProperties;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisLoginInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringJUnitConfig({
        ParsingKuisAuthManager.class, KuisLoginInfo.class, RequestBodyEncoder.class,
        RestTemplateConfig.class, JsonConfig.class, ParsingKuisAuthProperties.class
})
@EnableConfigurationProperties(value = ParsingKuisAuthProperties.class)
@TestPropertySource(locations = "classpath:test-constants.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ParsingKuisAuthManagerTest {

    private final String failResponseBody = "{\"ERRMSGINFO\":{\"ERRMSG\":\"건국대학교에 허가되지 않은 접근입니다. 반복시도시 내부규정에 따라 해당 계정정보를 차단하며 경우에 따라 민형사상의 책임을 질 수 있습니다.\",\"STATUSCODE\":-2000,\"ERRCODE\":\"건국대학교에 허가되지 않은 접근입니다. 반복시도시 내부규정에 따라 해당 계정정보를 차단하며 경우에 따라 민형사상의 책임을 질 수 있습니다.\"}}";
    private final String successResponseBody = "{\"_METADATA_\":{\"success\":true}}";
    private final ParsingKuisAuthProperties parsingKuisAuthProperties;
    private final KuisAuthManager kuisAuthManager;
    private final String apiSkeleton;

    private MockRestServiceServer server;
    private RestTemplate restTemplate;

    public ParsingKuisAuthManagerTest(
            KuisAuthManager parsingKuisAuthManager,
            RestTemplate restTemplate,
            ParsingKuisAuthProperties parsingKuisAuthProperties,
            @Value("${auth.api-skeleton-file-path}") String apiSkeletonFilePath) throws IOException
    {
        this.kuisAuthManager = parsingKuisAuthManager;
        this.restTemplate = restTemplate;
        this.parsingKuisAuthProperties = parsingKuisAuthProperties;
        apiSkeleton = readApiSkeleton(apiSkeletonFilePath);
    }

    @BeforeEach
    void setUpBefore() {
        server = MockRestServiceServer.createServer(restTemplate);
        kuisAuthManager.forceRenewing();
    }

    @Test
    @Order(1)
    @DisplayName("성공 - 로그인 후 세션ID 권한 획득")
    void success() {
        // given
        server.expect(requestTo(parsingKuisAuthProperties.getApiSkeletonProducerUrl())).
                andRespond(withSuccess().body(apiSkeleton));

        server.expect(requestTo(parsingKuisAuthProperties.getLoginUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().headers(buildJSessionHeader()).body(successResponseBody));

        // when
        String sessionId = kuisAuthManager.getSessionId();

        // then
        server.verify();
        assertEquals(parsingKuisAuthProperties.getSession(), sessionId);
    }

    @Test
    @Order(2)
    @DisplayName("성공 - 기존에 사용한 세션ID 반환")
    void successWithSessionCache() {
        // given
        server.expect(times(1), requestTo(parsingKuisAuthProperties.getApiSkeletonProducerUrl()))
                .andRespond(withSuccess().body(apiSkeleton));

        server.expect(times(1), requestTo(parsingKuisAuthProperties.getLoginUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().headers(buildJSessionHeader()).body(successResponseBody));

        // when
        String sessionId = kuisAuthManager.getSessionId();
        String secondSessionId = kuisAuthManager.getSessionId();

        // then
        server.verify();
        assertEquals(parsingKuisAuthProperties.getSession(), sessionId);
        assertEquals(parsingKuisAuthProperties.getSession(), secondSessionId);
    }

    @Test
    @Order(3)
    @DisplayName("실패 - 응답 body가 없음")
    void failByNoBody() {
        // given
        server.expect(requestTo(parsingKuisAuthProperties.getApiSkeletonProducerUrl()))
                .andRespond(withSuccess().body(apiSkeleton));

        server.expect(requestTo(parsingKuisAuthProperties.getLoginUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // when
        InternalLogicException e = assertThrows(InternalLogicException.class, kuisAuthManager::getSessionId);

        // then
        assertEquals(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY, e.getErrorCode());
    }

    @Test
    @Order(4)
    @DisplayName("실패 - 응답 body에 success 문자열이 없음 (kuis 로그인 방식이 바뀜 or api skeleton 최신화 안됨)")
    void failByNoSuccessStringInBody() {
        // given
        server.expect(requestTo(parsingKuisAuthProperties.getApiSkeletonProducerUrl()))
                .andRespond(withSuccess().body(apiSkeleton));

        server.expect(requestTo(parsingKuisAuthProperties.getLoginUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().body(failResponseBody));

        // when
        InternalLogicException e = assertThrows(InternalLogicException.class, kuisAuthManager::getSessionId);

        // then
        assertEquals(ErrorCode.KU_LOGIN_BAD_RESPONSE, e.getErrorCode());
    }

    @AfterEach
    void setUpAfter() {
        kuisAuthManager.forceRenewing();
    }

    private static HttpHeaders buildJSessionHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Set-Cookie", "JSESSIONID=test_session_id;");
        return httpHeaders;
    }

    private String readApiSkeleton(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resourceToString(resource.getInputStream());
    }

    private String resourceToString(InputStream inputStream) throws IOException {
        return FileCopyUtils.copyToString(new InputStreamReader(inputStream));
    }
}

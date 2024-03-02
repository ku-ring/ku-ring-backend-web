package com.kustacks.kuring.worker.scrap.client.auth;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.worker.scrap.client.auth.property.ParsingKuisAuthProperties;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


class ParsingKuisAuthManagerTest extends IntegrationTestSupport {

    @Autowired
    private ParsingKuisAuthProperties parsingKuisAuthProperties;

    @Autowired
    private ParsingKuisAuthManager kuisAuthManager;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth.api-skeleton-file-path}")
    private String apiSkeletonFilePath;

    private MockRestServiceServer server;

    @BeforeEach
    void setUpBefore() {
        server = MockRestServiceServer.createServer(restTemplate);
        kuisAuthManager.forceRenewing();
    }

    @Test
    @DisplayName("로그인에 성공한 후 세션ID 권한을 획득한다")
    void success() throws IOException {
        // given
        String apiSkeleton = readApiSkeleton(apiSkeletonFilePath);
        String successResponseBody = "{\"_METADATA_\":{\"success\":true}}";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Set-Cookie", "JSESSIONID=test_session_id;");

        server.expect(times(1), requestTo(parsingKuisAuthProperties.getApiSkeletonProducerUrl()))
                .andRespond(withSuccess().body(apiSkeleton));

        server.expect(times(1), requestTo(parsingKuisAuthProperties.getLoginUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().headers(httpHeaders).body(successResponseBody));

        // when
        String sessionId = kuisAuthManager.getSessionId();

        // then
        assertAll(
                () -> server.verify(),
                () -> assertThat(parsingKuisAuthProperties.getSession()).isEqualTo(sessionId)
        );
    }

    @Test
    @DisplayName("로그인 요청에 대한 서버의 응답 body가 없는경우 예외를 발생시킨다")
    void failByNoBody() throws IOException {
        // given
        String apiSkeleton = readApiSkeleton(apiSkeletonFilePath);

        server.expect(times(1), requestTo(parsingKuisAuthProperties.getApiSkeletonProducerUrl()))
                .andRespond(withSuccess().body(apiSkeleton));

        server.expect(times(1), requestTo(parsingKuisAuthProperties.getLoginUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // when
        ThrowingCallable actual = kuisAuthManager::getSessionId;

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(InternalLogicException.class);
    }

    @Test
    @DisplayName("로그인 요청에 대한 서버의 응답 body에 success 문자열이 없는경우 예외를 발생시킨다 (kuis 로그인 방식이 바뀜 or api skeleton 최신화 안됨)")
    void failByNoSuccessStringInBody() throws IOException {
        // given
        String apiSkeleton = readApiSkeleton(apiSkeletonFilePath);
        String failResponseBody = "{\"ERRMSGINFO\":{\"ERRMSG\":\"건국대학교에 허가되지 않은 접근입니다. 반복시도시 내부규정에 따라 해당 계정정보를 차단하며 경우에 따라 민형사상의 책임을 질 수 있습니다.\",\"STATUSCODE\":-2000,\"ERRCODE\":\"건국대학교에 허가되지 않은 접근입니다. 반복시도시 내부규정에 따라 해당 계정정보를 차단하며 경우에 따라 민형사상의 책임을 질 수 있습니다.\"}}";

        server.expect(times(1), requestTo(parsingKuisAuthProperties.getApiSkeletonProducerUrl()))
                .andRespond(withSuccess().body(apiSkeleton));

        server.expect(times(1), requestTo(parsingKuisAuthProperties.getLoginUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().body(failResponseBody));

        // when
        ThrowingCallable actual = kuisAuthManager::getSessionId;

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(InternalLogicException.class);
    }

    private String readApiSkeleton(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resourceToString(resource.getInputStream());
    }

    private String resourceToString(InputStream inputStream) throws IOException {
        return FileCopyUtils.copyToString(new InputStreamReader(inputStream));
    }
}

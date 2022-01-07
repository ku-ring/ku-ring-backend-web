package com.kustacks.kuring.kuapi.notice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisLoginRequestBody;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RenewSessionKuisAuthManager implements KuisAuthManager {

    @Value("${auth.login-url}")
    private String loginUrl;

    @Value("${auth.user-agent}")
    private String loginUserAgent;

    @Value("${auth.referer}")
    private String loginReferer;

    @Value("${auth.api-skeleton-producer-url}")
    private String apiSkeletonProducerUrl;

    @Value("${auth.session}")
    private String sessionId;

    private final KuisLoginRequestBody kuisLoginRequestBody;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final int LOGIN_RETRY_PERIOD = 1000 * 10;

    private String loginRequestBody;
    private boolean sessionNeedToBeRenew; // 세션 유효기간이 남아있어야 하지만, 알 수 없는 오류로 인해 세션이 유효하지 않은 경우 true로 설정됨
    private boolean apiSkeletonNeedToBeRenew;

    public RenewSessionKuisAuthManager(
            KuisLoginRequestBody kuisLoginRequestBody,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.kuisLoginRequestBody = kuisLoginRequestBody;
        this.sessionNeedToBeRenew = true;
        this.apiSkeletonNeedToBeRenew = true;
        this.loginRequestBody = null;
    }

    @Override
//    @Retryable(value = {InternalLogicException.class}, backoff = @Backoff(delay = LOGIN_RETRY_PERIOD))
    public String getSessionId() {

        if(!sessionNeedToBeRenew && !apiSkeletonNeedToBeRenew) {
            log.info("세션아이디 갱신 안하고 바로 리턴");
            return sessionId;
        }

        log.info("세션아이디 갱신시작");

        // request body api skeleton 갱신
        StringBuilder loginRequestBodyStringBuilder = null;
        if(apiSkeletonNeedToBeRenew) {
            try {
                String apiSkeletonStr = restTemplate.getForObject(apiSkeletonProducerUrl, String.class);
                loginRequestBodyStringBuilder = renewApiSkeleton(apiSkeletonStr);
                apiSkeletonNeedToBeRenew = false;
            } catch(RestClientException e) {
                throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_GET_API_SKELETON, e);
            } catch(JsonProcessingException e) {
                throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_PARSE_API_SKELETON, e);
            }
        }

        // 로그인 헤더
        HttpHeaders loginRequestHeader = createLoginRequestHeader();

        // 로그인 본문
        // 로그인 요청 바디가 갱신되었을 경우에만 요청 바디를 새로 만든다.
        if(loginRequestBodyStringBuilder != null) {
            loginRequestBody = loginRequestBodyStringBuilder.append(KuisRequestBody.toUrlEncodedString(kuisLoginRequestBody)).toString();
        }

        // 로그인 요청
        HttpEntity<String> loginRequestEntity = new HttpEntity<>(loginRequestBody, loginRequestHeader);
        ResponseEntity<String> loginResponse;
        try {
            loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequestEntity, String.class);
        } catch(RestClientException e) {
            sessionNeedToBeRenew = true; // loginUrl이 틀리지 않는 이상 발생할 일이 없어보이는 오류
            throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_LOGIN, e);
        }

        // 로그인 요청에 대한 응답 메세지의 body 확인
        boolean isLoginSuccess = checkLoginResponseBody(loginResponse);
        if(!isLoginSuccess) {
            sessionNeedToBeRenew = true;
            apiSkeletonNeedToBeRenew = true;
            throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE);
        } else {
            sessionNeedToBeRenew = false;
        }

        log.info("세션아이디 갱신완료");
        return this.sessionId;
    }

    public void forceRenewing() {
        this.sessionNeedToBeRenew = true;
//        this.apiSkeletonNeedToBeRenew = true;
    }

    private boolean checkLoginResponseBody(ResponseEntity<String> loginResponse) {

        String body = loginResponse.getBody();
        if(body == null) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY);
        } else {
            log.info(body);
            return body.contains("success");
        }
    }

    private HttpHeaders createLoginRequestHeader() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Referer", loginReferer);
        httpHeaders.add("Accept", "*/*");
        httpHeaders.add("Accept-Encoding", "gzip, deflate, br");
        httpHeaders.add("User-Agent", loginUserAgent);
        httpHeaders.add("Cookie", sessionId);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return httpHeaders;
    }

    private StringBuilder renewApiSkeleton(String responseBody) throws JsonProcessingException {

        Map<String, List<Map<String, String>>> apiSkeletonMap = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, String>> fields = apiSkeletonMap.get("fields");
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> keyValue : fields) {
            String encodedKey = URLEncoder.encode(keyValue.get("key"), StandardCharsets.UTF_8);
            String encodedValue = URLEncoder.encode(keyValue.get("value"), StandardCharsets.UTF_8);

            encodedKey = encodedKey.replaceAll("\\+", "%20");
            encodedValue = encodedValue.replaceAll("\\+", "%20");

            sb.append(encodedKey).append("=").append(encodedValue).append("&");
        }

        return sb;
    }

    private String parseCookieHeader(ResponseEntity<String> indexResponse) {

        String sessionId = null;
        HttpHeaders responseHeaders = indexResponse.getHeaders();

        if(responseHeaders.containsKey("Set-Cookie")) {
            List<String> setCookieValues = responseHeaders.get("Set-Cookie");

            if(setCookieValues == null || setCookieValues.isEmpty()) {
                throw new InternalLogicException(ErrorCode.KU_LOGIN_EMPTY_COOKIE);
            }

            String setCookieValue = null;
            for (String s : setCookieValues) {
                if(s.contains("JSESSIONID")) {
                    setCookieValue = s;
                    break;
                }
            }
            if(setCookieValue == null) {
                throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_JSESSION);
            }

            // 위 조건문에서 JSESSIONID 문자열 포함 여부를 확인했으므로
            // 여기서는 별다른 예외 체크 없이 그 값을 sessionId에 담기만 한다.
            String[] cookieValues = setCookieValue.split(";");
            for (String value : cookieValues) {
                if(value.contains("JSESSIONID")) {
                    sessionId = value;
                    break;
                }
            }
        } else {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_COOKIE_HEADER);
        }

        return sessionId;
    }
}

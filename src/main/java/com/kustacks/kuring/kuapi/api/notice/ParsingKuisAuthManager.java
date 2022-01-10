package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisLoginRequestBody;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisRequestBody;
import com.kustacks.kuring.util.encoder.Encoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ParsingKuisAuthManager implements KuisAuthManager {

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
    private final Encoder encoder;

    private String loginRequestBody;
    private boolean sessionNeedToBeRenew; // 세션 유효기간이 남아있어야 하지만, 알 수 없는 오류로 인해 세션이 유효하지 않은 경우 true로 설정됨
    private boolean apiSkeletonNeedToBeRenew;
    private boolean isLoginPossible;
    private final String parsingPattern = "submit\\.addParameter[(]\"(.{5,7})\",\"(.{5,7})\"[)]";
    private final Pattern pattern;

    public ParsingKuisAuthManager(
            KuisLoginRequestBody kuisLoginRequestBody,
            RestTemplate restTemplate,
            Encoder requestBodyEncoder) {

        this.restTemplate = restTemplate;
        this.kuisLoginRequestBody = kuisLoginRequestBody;
        this.encoder = requestBodyEncoder;
        this.sessionNeedToBeRenew = true;
        this.apiSkeletonNeedToBeRenew = true;
        this.isLoginPossible = true;
        this.loginRequestBody = null;

        this.pattern  = Pattern.compile(parsingPattern);
    }

    @Override
    public String getSessionId() {

        if(!isLoginPossible) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_IMPOSSIBLE);
        }
        
        // apiSkeletonNeedToBeRenew가 true 이면 sessionNeedToBeRenew는 항상 true이므로 sessionNeedToBeRenew만 검사함.
        if(!sessionNeedToBeRenew) {
            log.info("세션아이디 갱신 안하고 바로 리턴");
            return sessionId;
        }

        log.info("세션아이디 갱신시작");

        // request body api skeleton 갱신
        StringBuilder loginRequestBodyStringBuilder;
        try {
            String apiSkeletonStr = restTemplate.getForObject(apiSkeletonProducerUrl, String.class);
            loginRequestBodyStringBuilder = renewApiSkeleton(apiSkeletonStr);
            apiSkeletonNeedToBeRenew = false;
        } catch(RestClientException e) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_GET_API_SKELETON, e);
        }

        // 로그인 헤더
        HttpHeaders loginRequestHeader = createLoginRequestHeader();

        // 로그인 본문
        loginRequestBody = loginRequestBodyStringBuilder.append(KuisRequestBody.toUrlEncodedString(kuisLoginRequestBody)).toString();

        log.info("body = {}", loginRequestBody);

        // 로그인 요청
        HttpEntity<String> loginRequestEntity = new HttpEntity<>(loginRequestBody, loginRequestHeader);
        ResponseEntity<String> loginResponse;
        try {
            loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequestEntity, String.class);
        } catch(RestClientException e) {
            sessionNeedToBeRenew = true; // loginUrl이 틀렸거나 kuis서버가 불안정해서 5xx 에러를 뱉는 경우
            throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_LOGIN, e);
        }

        // 로그인 요청에 대한 응답 메세지의 body 확인
        boolean isLoginSuccess = checkLoginResponseBody(loginResponse);;
        if(!isLoginSuccess) {
            // 잘못된 응답 body가 왔다면 로그인 방식이 변경되었다고 추측해야한다.
            isLoginPossible = false;
            throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE);
        } else {
            sessionNeedToBeRenew = false;
        }

        log.info("세션아이디 갱신완료");
        return this.sessionId;
    }

    public void forceRenewing() {
        this.sessionNeedToBeRenew = true;
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

    private StringBuilder renewApiSkeleton(String responseBody) {

        Matcher matcher = pattern.matcher(responseBody);
        StringBuilder sb = new StringBuilder();
        while(matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            key = encoder.encode(key);
            value = encoder.encode(value);

            sb.append(key).append("=").append(value).append("&");
            log.info("key = {}, value = {}", matcher.group(1), matcher.group(2));
        }

        return sb;
    }
}

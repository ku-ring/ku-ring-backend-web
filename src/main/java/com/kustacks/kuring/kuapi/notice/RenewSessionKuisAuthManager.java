package com.kustacks.kuring.kuapi.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisLoginRequestBody;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class RenewSessionKuisAuthManager implements KuisAuthManager {

    @Value("${auth.login-url}")
    private String loginUrl;

    @Value("${auth.user-agent}")
    private String loginUserAgent;

    @Value("${auth.referer}")
    private String loginReferer;

    private final KuisLoginRequestBody kuisLoginRequestBody;
    private final RestTemplate restTemplate;

    private final int LOGIN_RETRY_PERIOD = 1000 * 10;
//    private final int SESSION_DURATION = 5400; // 1시간 30분
    private final int SESSION_DURATION = 30;

    private boolean needToBeRenew; // 세션 유효기간이 남아있어야 하지만, 알 수 없는 오류로 인해 세션이 유효하지 않은 경우 true로 설정됨

    @Value("${auth.session}")
    private String sessionId;

    public RenewSessionKuisAuthManager(
            @Value("${auth.login-url}") String loginUrl,
            @Value("${auth.user-agent}") String loginUserAgent,
            @Value("${auth.referer}") String loginReferer,
            KuisLoginRequestBody kuisLoginRequestBody,
            RestTemplate restTemplate) {

        this.loginUrl = loginUrl;
        this.loginUserAgent = loginUserAgent;
        this.loginReferer = loginReferer;

        this.restTemplate = restTemplate;
        this.kuisLoginRequestBody = kuisLoginRequestBody;
        this.needToBeRenew = true;
    }

    @Override
    @Retryable(value = {InternalLogicException.class}, backoff = @Backoff(delay = LOGIN_RETRY_PERIOD))
    public String getSessionId() {

        if(!needToBeRenew) {
            log.info("세션아이디 갱신 안하고 바로 리턴");
            return sessionId;
        }

        log.info("세션아이디 갱신시작");

        // 로그인 헤더
        HttpHeaders loginRequestHeader = createLoginRequestHeader();

        // 로그인 본문
        String loginRequestBody = KuisRequestBody.toUrlEncodedString(kuisLoginRequestBody);

        // 로그인 요청
        HttpEntity<String> loginRequestEntity = new HttpEntity<>(loginRequestBody, loginRequestHeader);

        ResponseEntity<String> loginResponse; // 응답 제대로 받지 못하면 아래 로직에서 처리하므로, 세션 업데이트에서 null일 확률은 없음

        try {
            loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequestEntity, String.class);
        } catch(RestClientException e) {
            needToBeRenew = true;
            throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_LOGIN, e);
        }

        // 로그인 요청에 대한 응답 메세지의 body 확인
        boolean isLoginSuccess = checkLoginResponseBody(loginResponse);
        if(!isLoginSuccess) {
            needToBeRenew = true;
            throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE);
        }

        needToBeRenew = false;

        log.info("세션아이디 갱신완료");
        return this.sessionId;
    }

    public void forceRenewing() {
        this.needToBeRenew = true;
    }

    private boolean checkLoginResponseBody(ResponseEntity<String> loginResponse) {

        String body = loginResponse.getBody();
        if(body == null) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY);
        } else {
            return body.contains("success");
        }
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
}

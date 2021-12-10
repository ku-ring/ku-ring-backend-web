package com.kustacks.kuring.kuapi.notice;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisLoginRequestBody;
import com.kustacks.kuring.kuapi.notice.dto.request.KuisRequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RenewSessionKuisAuthManager implements KuisAuthManager {

    @Value("${auth.login-url}")
    private String loginUrl;

    @Value("${auth.user-agent}")
    private String loginUserAgent;

    @Value("${auth.referer}")
    private String loginReferer;

    private String sessionId;

    private final KuisLoginRequestBody kuisLoginRequestBody;

    private final int MAX_KU_LOGIN_TRY = 3;
    private final int LOGIN_RETRY_PERIOD = 1000 * 10;

    public RenewSessionKuisAuthManager(KuisLoginRequestBody kuisLoginRequestBody) {

        this.kuisLoginRequestBody = kuisLoginRequestBody;

        sessionId = null;
    }

    @Override
    public String getSessionId() throws InterruptedException {

        // 로그인 헤더
        HttpHeaders loginRequestHeader = createLoginRequestHeader();

        // 로그인 본문
        String loginRequestBody = KuisRequestBody.toUrlEncodedString(kuisLoginRequestBody);

        // 로그인 요청
        HttpEntity<String> loginRequestEntity = new HttpEntity<>(loginRequestBody, loginRequestHeader);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> loginResponse = null; // 응답 제대로 받지 못하면 아래 로직에서 처리하므로, 세션 업데이트에서 null일 확률은 없음

        int kuLoginTryCount = 0;
        while(kuLoginTryCount < MAX_KU_LOGIN_TRY) {
            ++kuLoginTryCount;
            try {
                loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequestEntity, String.class);
                break;
            } catch(RestClientException e) {
                if(kuLoginTryCount == MAX_KU_LOGIN_TRY) {
                    throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_LOGIN, e);
                }

                Thread.sleep(LOGIN_RETRY_PERIOD);
            }
        }

        // 로그인 요청에 대한 응답 메세지의 Set-Cookie 헤더 파싱
        return parseCookieHeader(loginResponse);
//            log.error("[KuLoginException] KUIS 로그인 응답 메세지를 파싱하는 중 오류가 발생했습니다.");
//            Sentry.captureException(e);
//            return;
    }

    private String parseCookieHeader(ResponseEntity<String> loginResponse) {

        String body = loginResponse.getBody();
        String sessionId = null;

        if(body == null) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY);
        } else {
            if(body.contains("success")) {
                HttpHeaders responseHeaders = loginResponse.getHeaders();

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
            }
            // 로그인 응답의 body를 가져올 수 없는 상태
            // 로그를 남기고 개발자에게 알람을 따로 주는 방안이 좋을듯
            else {
                throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE);
            }
        }

        return sessionId;
    }

    private HttpHeaders createLoginRequestHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Referer", loginReferer);
        httpHeaders.add("Accept", "*/*");
        httpHeaders.add("Accept-Encoding", "gzip, deflate, br");
        httpHeaders.add("User-Agent", loginUserAgent);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return httpHeaders;
    }
}

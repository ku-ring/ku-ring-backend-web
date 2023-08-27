package com.kustacks.kuring.worker.scrap.client.auth;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.client.auth.property.ParsingKuisAuthProperties;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisLoginInfo;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisInfo;
import com.kustacks.kuring.common.utils.encoder.Encoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ParsingKuisAuthManager implements KuisAuthManager {

    private final static String PARSING_PATTERN = "submit\\.addParameter[(]\"(.{5,7})\",\"(.{5,7})\"[)]";
    private final static String SESSION_PATTERN = "JSESSIONID=(.*?);";
    private final KuisLoginInfo kuisLoginRequestBody;
    private final RestTemplate restTemplate;
    private final Encoder encoder;

    private ParsingKuisAuthProperties parsingKuisAuthProperties;
    private Pattern apiSkeletonPattern;
    private Pattern sessionPattern;
    private String loginRequestBody;
    private boolean sessionNeedToBeRenew; // 세션 유효기간이 남아있어야 하지만, 알 수 없는 오류로 인해 세션이 유효하지 않은 경우 true로 설정됨
    private boolean isLoginPossible;

    public ParsingKuisAuthManager(KuisLoginInfo kuisLoginRequestBody, RestTemplate restTemplate,
                                  Encoder requestBodyEncoder, ParsingKuisAuthProperties parsingKuisAuthProperties) {
        this.kuisLoginRequestBody = kuisLoginRequestBody;
        this.restTemplate = restTemplate;
        this.encoder = requestBodyEncoder;
        this.parsingKuisAuthProperties = parsingKuisAuthProperties;
        initProperties();
    }

    @Override
    public String getSessionId() {
        if (!this.isLoginPossible) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_IMPOSSIBLE);
        }

        if (!this.sessionNeedToBeRenew) {  // apiSkeletonNeedToBeRenew가 true 이면 sessionNeedToBeRenew는 항상 true이므로 sessionNeedToBeRenew만 검사함.
            log.debug("세션아이디 갱신 안하고 바로 리턴");
            return parsingKuisAuthProperties.getSession();
        }

        log.info("세션아이디 갱신시작");

        StringBuilder loginRequestBodyStringBuilder = refreshLoginRequestBody();

        HttpEntity<String> loginRequestEntity = requestLoginAndReturnHttpEntity(loginRequestBodyStringBuilder);

        loginAndRefreshSession(loginRequestEntity);

        log.info("세션아이디 갱신완료");

        return this.parsingKuisAuthProperties.getSession();
    }

    public void forceRenewing() {
        this.sessionNeedToBeRenew = true;
    }

    private void initProperties() {
        this.sessionNeedToBeRenew = true;
        this.isLoginPossible = true;
        this.loginRequestBody = null;
        this.apiSkeletonPattern = Pattern.compile(PARSING_PATTERN);
        this.sessionPattern = Pattern.compile(SESSION_PATTERN);
    }

    private void loginAndRefreshSession(HttpEntity<String> loginRequestEntity) {
        try {
            ResponseEntity<String> loginResponse = restTemplate.postForEntity(parsingKuisAuthProperties.getLoginUrl(), loginRequestEntity, String.class);

            if (isInvalidLoginResponse(loginResponse)) {
                this.isLoginPossible = false; // 잘못된 응답 body가 왔다면 로그인 방식이 변경되었다고 추측해야한다.
                throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE);
            } else {
                this.sessionNeedToBeRenew = false;
                this.parsingKuisAuthProperties.setSession(extractSessionId(loginResponse));
            }
        } catch (RestClientException e) {
            this.sessionNeedToBeRenew = true; // loginUrl이 틀렸거나 kuis서버가 불안정해서 5xx 에러를 뱉는 경우
            throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_LOGIN, e);
        }
    }

    private String extractSessionId(ResponseEntity<String> loginResponse) {
        List<String> cookies = loginResponse.getHeaders().get("Set-Cookie");
        for (String cookie : cookies) {
            if (cookie.contains("JSESSIONID")) {
                Matcher matcher = sessionPattern.matcher(cookie);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }

        this.sessionNeedToBeRenew = true;
        throw new InternalLogicException(ErrorCode.KU_LOGIN_BAD_RESPONSE);
    }

    private StringBuilder refreshLoginRequestBody() throws InternalLogicException {
        try {
            String apiSkeletonString = restTemplate.getForObject(parsingKuisAuthProperties.getApiSkeletonProducerUrl(), String.class);
            return refreshApiSkeleton(apiSkeletonString);
        } catch (RestClientException e) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_GET_API_SKELETON, e);
        }
    }

    private HttpEntity<String> requestLoginAndReturnHttpEntity(StringBuilder loginRequestBodyStringBuilder) {
        // 로그인 헤더
        HttpHeaders loginRequestHeader = createLoginRequestHeader();

        // 로그인 본문
        this.loginRequestBody = loginRequestBodyStringBuilder.append(KuisInfo.toUrlEncodedString(kuisLoginRequestBody)).toString();

        // 로그인 요청 객체
        return new HttpEntity<>(loginRequestBody, loginRequestHeader);
    }

    private boolean isInvalidLoginResponse(ResponseEntity<String> loginResponse) {
        String body = loginResponse.getBody();
        if (body == null) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY);
        }

        log.debug(body);
        return !body.contains("success");
    }

    private HttpHeaders createLoginRequestHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Referer", parsingKuisAuthProperties.getReferer());
        httpHeaders.add("Accept", "*/*");
        httpHeaders.add("Accept-Encoding", "gzip, deflate, br");
        httpHeaders.add("User-Agent", parsingKuisAuthProperties.getUserAgent());
        httpHeaders.add("Cookie", parsingKuisAuthProperties.getSession());
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return httpHeaders;
    }

    private StringBuilder refreshApiSkeleton(String responseBody) {
        Matcher matcher = apiSkeletonPattern.matcher(responseBody);

        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
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

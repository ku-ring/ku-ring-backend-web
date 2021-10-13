package com.kustacks.kuring.kuapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.notice.NoticeRepository;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.request.*;
import com.kustacks.kuring.kuapi.response.KuisNoticeDTO;
import com.kustacks.kuring.kuapi.response.KuisNoticeResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
public class KuApiWatcher {

    @Value("${notice.request-url}")
    private String noticeUrl;

    @Value("${notice.referer}")
    private String noticeReferer;

    @Value("${auth.login-url}")
    private String loginUrl;

    @Value("${auth.user-agent}")
    private String loginUserAgent;

    private final ObjectMapper objectMapper;

    private final KuisLoginRequestBody kuisLoginRequestBody;
    private final Map<NoticeCategory, KuisNoticeRequestBody> noticeRequestBodies;

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;

    private String cookieValue = "";
    private Map<String, Category> categoryMap;

    public KuApiWatcher(
            ObjectMapper objectMapper,
            KuisLoginRequestBody kuisLoginRequestBody,

            BachelorKuisNoticeRequestBody bachelorNoticeRequestBody,
            ScholarshipKuisNoticeRequestBody scholarshipNoticeRequestBody,

            NoticeRepository noticeRepository,
            CategoryRepository categoryRepository
    ) {
        this.objectMapper = objectMapper;

        this.kuisLoginRequestBody = kuisLoginRequestBody;

        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;

        noticeRequestBodies = new LinkedHashMap<>();
        noticeRequestBodies.put(NoticeCategory.BACHELOR, bachelorNoticeRequestBody);
        noticeRequestBodies.put(NoticeCategory.SCHOLARSHIP, scholarshipNoticeRequestBody);
    }


    @Scheduled(cron = "* 0/10 * * * *")
    public void watchAndUpdateNotice() {

        // 로그인 헤더
        HttpHeaders loginRequestHeader = createLoginRequestHeader();

        // 로그인 본문
        String loginRequestBody = KuisRequestBody.toUrlEncodedString(kuisLoginRequestBody);

        // 로그인 요청
        HttpEntity<String> loginRequestEntity = new HttpEntity<>(loginRequestBody, loginRequestHeader);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequestEntity, String.class);

        // 세션 업데이트
        updateSession(loginResponse);



        // 공지 요청 헤더
        HttpHeaders noticeRequestHeader = createNoticeRequestHeader();

        // 공지 요청
        Map<String, KuisNoticeResponseBody> kuisNoticeResponseBodies = new LinkedHashMap<>(); // 수신한 공지 데이터를 저장할 변수
        for (NoticeCategory noticeCategory : noticeRequestBodies.keySet()) {
            String categoryName = noticeCategory.getValue();

            KuisNoticeRequestBody kuisNoticeRequestBody = noticeRequestBodies.get(noticeCategory);

            String encodedNoticeRequestBody = KuisRequestBody.toUrlEncodedString(kuisNoticeRequestBody);
            HttpEntity<String> noticeRequestEntity = new HttpEntity<>(encodedNoticeRequestBody, noticeRequestHeader);
            ResponseEntity<String> noticeResponse = restTemplate.exchange(noticeUrl, HttpMethod.POST, noticeRequestEntity, String.class);

            log.info("{} response body = {}", noticeCategory, noticeResponse.getBody());

            try {
                KuisNoticeResponseBody tmp = objectMapper.readValue(noticeResponse.getBody(), KuisNoticeResponseBody.class);
                if(tmp == null) {
                    log.warn("JSON Parsing result is null");
                } else {
                    for (KuisNoticeDTO kuisNoticeDTO : tmp.getKuisNoticeDTOList()) {
                        log.info("articleId = {}, postedDt = {}, subject = {}", kuisNoticeDTO.getArticleId(), kuisNoticeDTO.getPostedDate(), kuisNoticeDTO.getSubject());
                    }
                }
                kuisNoticeResponseBodies.put(categoryName, tmp);
            } catch(JsonProcessingException e) {
                throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
            }
        }


        // DB에 있는 공지 데이터 카테고리별로 꺼내와서
        // kuisNoticeResponseBody에 있는 데이터가 DB에는 없는 경우 -> DB에 공지 추가
        // DB에 있는 데이터가 kuisNoticeResponseBody에는 없는 경우 -> DB에 공지 삭제
        updateNotice(kuisNoticeResponseBodies);
    }

    private void updateNotice(Map<String, KuisNoticeResponseBody> kuisNoticeResponseBodies) {
        if(categoryMap == null) {
            categoryMap = categoryRepository.findAllMap();
        }

        for (String categoryName : kuisNoticeResponseBodies.keySet()) {

            Category noticeCategory = categoryMap.get(categoryName);

            // categoryName에 대응하는 kuis에서 수신한 공지 데이터
            KuisNoticeResponseBody kuisNoticeResponseBody = kuisNoticeResponseBodies.get(categoryName);
            List<KuisNoticeDTO> kuisNoticeDTOList = kuisNoticeResponseBody.getKuisNoticeDTOList();

            // categoryName에 대응하는 DB에 존재하는 공지 데이터
            Map<String, Notice> dbNoticeMap = noticeRepository.findByCategoryMap(noticeCategory);

            // kuisNoticeDTOList를 순회하면서
            // 현재 kuisNotice가 dbNoticeList에 있으면 dbNoticeList에서 해당 공지 없애고(실제 DB에는 아무런 작업 안함)
            // dbNoticeList에 없다면 공지 추가 (실제 DB에 추가)
            // 작업이 끝난 후 dbNoticeList에 공지가 남아있다면, 해당 공지들은 DB에서 삭제 (실제 DB에 삭제)
            List<Notice> newNotices = new LinkedList<>(); // DB에 추가할 공지 임시 저장
            Iterator<KuisNoticeDTO> kuisNoticeIterator = kuisNoticeDTOList.iterator();
            while(kuisNoticeIterator.hasNext()) {
                KuisNoticeDTO kuisNotice = kuisNoticeIterator.next();
                Notice notice = dbNoticeMap.get(kuisNotice.getArticleId());
                if(notice == null) {
                    newNotices.add(kuisNotice.toEntity(noticeCategory));
                } else {
                    kuisNoticeIterator.remove();
                    dbNoticeMap.remove(kuisNotice.getArticleId());
                }
            }

            // 업데이트로 인해 없어져야될 공지 삭제
            Collection<Notice> removedNotices = dbNoticeMap.values();
            noticeRepository.deleteAll(removedNotices);

            // 업데이트로 인해 새로 생성된 공지 삽입
            noticeRepository.saveAll(newNotices);
        }
    }

    private void updateSession(ResponseEntity<String> loginResponse) {

        String body = loginResponse.getBody();

        if(body == null) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY);
        } else {
            if(body.contains("success")) {
                HttpHeaders responseHeaders = loginResponse.getHeaders();

                if(responseHeaders.containsKey("Set-Cookie")) {
                    List<String> setCookieValues = responseHeaders.get("Set-Cookie"); // TODO: 고쳐야됨

                    for (String setCookieValue : setCookieValues) {
                        log.info("setCookieValue = {}", setCookieValue);
                    }

                    if(!setCookieValues.isEmpty()) {
                        String setCookieValue = null;
                        boolean isJsessionExist = false;
                        for (String s : setCookieValues) {
                            if(s.contains("JSESSIONID")) {
                                isJsessionExist = true;
                                setCookieValue = s;
                                break;
                            }
                        }
                        if(!isJsessionExist) {
                            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_JSESSION);
                        }

                        String[] cookieValues = setCookieValue.split(";");
                        for (String value : cookieValues) {
                            if(value.contains("JSESSIONID")) {
                                cookieValue = value;
                                log.debug(cookieValue);
                                break;
                            }
                        }
                    } else {
                        throw new InternalLogicException(ErrorCode.KU_LOGIN_EMPTY_COOKIE);
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
    }

    private HttpHeaders createLoginRequestHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Referer", noticeReferer); // TODO: noticeReferer과 동일한 상수여서 일단 noticeURL로 적었는데, 다른 변수로 구분해야 할까?
        httpHeaders.add("Accept", "*/*");
        httpHeaders.add("Accept-Encoding", "gzip, deflate, br");
        httpHeaders.add("User-Agent", loginUserAgent);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return httpHeaders;
    }

    private HttpHeaders createNoticeRequestHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Referer", noticeReferer);
        httpHeaders.add("Cookie", cookieValue);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return httpHeaders;
    }
}

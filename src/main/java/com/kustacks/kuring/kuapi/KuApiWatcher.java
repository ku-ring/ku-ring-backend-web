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
import com.kustacks.kuring.kuapi.response.LibraryNoticeDTO;
import com.kustacks.kuring.kuapi.response.LibraryNoticeResponseBody;
import com.kustacks.kuring.kuapi.response.LibraryDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Component
public class KuApiWatcher {

    @Value("${notice.request-url}")
    private String noticeUrl;

    @Value("${notice.referer}")
    private String noticeReferer;

    @Value("${library.request-url}")
    private String libraryUrl;

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
            EmploymentKuisNoticeRequestBody employmentKuisNoticeRequestBody,
            NationalKuisNoticeRequestBody nationalKuisNoticeRequestBody,
            StudentKuisNoticeRequestBody studentKuisNoticeRequestBody,
            IndustryUnivKuisNoticeRequestBody industryUnivKuisNoticeRequestBody,
            NormalKuisNoticeRequestBody normalKuisNoticeRequestBody,

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
        noticeRequestBodies.put(NoticeCategory.EMPLOYMENT, employmentKuisNoticeRequestBody);
        noticeRequestBodies.put(NoticeCategory.NATIONAL, nationalKuisNoticeRequestBody);
        noticeRequestBodies.put(NoticeCategory.STUDENT, studentKuisNoticeRequestBody);
        noticeRequestBodies.put(NoticeCategory.INDUSTRY_UNIV, industryUnivKuisNoticeRequestBody);
        noticeRequestBodies.put(NoticeCategory.NORMAL, normalKuisNoticeRequestBody);
    }


    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void watchAndUpdateNotice() {

        /*
            학사, 장학, 취창업, 국제, 학생, 산학, 일반 공지 갱신 (from kuis)
         */
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
            String categoryName = noticeCategory.getName();

            KuisNoticeRequestBody kuisNoticeRequestBody = noticeRequestBodies.get(noticeCategory);

            String encodedNoticeRequestBody = KuisRequestBody.toUrlEncodedString(kuisNoticeRequestBody);
            HttpEntity<String> noticeRequestEntity = new HttpEntity<>(encodedNoticeRequestBody, noticeRequestHeader);
            ResponseEntity<KuisNoticeResponseBody> noticeResponse = restTemplate.exchange(noticeUrl, HttpMethod.POST, noticeRequestEntity, KuisNoticeResponseBody.class);

            KuisNoticeResponseBody tmp = noticeResponse.getBody();
            if(tmp == null) {
                throw new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON);
            }
            kuisNoticeResponseBodies.put(categoryName, tmp);
        }

        // DB에 있는 공지 데이터 카테고리별로 꺼내와서
        // kuisNoticeResponseBody에 있는 데이터가 DB에는 없는 경우 -> DB에 공지 추가
        // DB에 있는 데이터가 kuisNoticeResponseBody에는 없는 경우 -> DB에 공지 삭제
        updateNotice(kuisNoticeResponseBodies);


        /*
            도서관 공지 갱신
         */
        int offset = 0;
        int max = 20;
        int reqIdx = 0;
        List<LibraryNoticeResponseBody> libraryNoticeResponseBodies = new LinkedList<>();
        while(reqIdx < 2) {
            String fullLibraryUrl = UriComponentsBuilder.fromUriString(libraryUrl).queryParam("offset", offset).queryParam("max", max).build().toString();
            ResponseEntity<LibraryNoticeResponseBody> libraryResponse = restTemplate.getForEntity(fullLibraryUrl, LibraryNoticeResponseBody.class);
            LibraryNoticeResponseBody libraryNoticeResponseBody = libraryResponse.getBody();
            if(libraryNoticeResponseBody == null) {
                throw new InternalLogicException(ErrorCode.LIB_CANNOT_PARSE_JSON);
            }

            boolean isLibraryRequestSuccess = libraryNoticeResponseBody.isSuccess();
            if(!isLibraryRequestSuccess) {
                throw new InternalLogicException(ErrorCode.LIB_BAD_RESPONSE);
            }

            offset = max;
            max = libraryNoticeResponseBody.getData().getTotalCount() - max;

            libraryNoticeResponseBodies.add(libraryNoticeResponseBody);
            ++reqIdx;
        }

        updateLibrary(libraryNoticeResponseBodies);
    }

    private void updateLibrary(List<LibraryNoticeResponseBody> libraryNoticeResponseBodies) {

        Map<String, Notice> dbLibraryNotices = noticeRepository.findByCategoryMap(categoryMap.get(NoticeCategory.LIBRARY.getName()));
        List<Notice> newLibraryNotices = new LinkedList<>();

        for (LibraryNoticeResponseBody libraryNoticeResponseBody : libraryNoticeResponseBodies) {
            LibraryDataDTO data = libraryNoticeResponseBody.getData();
            List<LibraryNoticeDTO> libraryNoticeDTOList = data.getList();
            Iterator<LibraryNoticeDTO> libraryNoticeIterator = libraryNoticeDTOList.iterator();

            Category libraryCategory = categoryMap.get(NoticeCategory.LIBRARY.getName());

            while(libraryNoticeIterator.hasNext()) {
                LibraryNoticeDTO libraryNotice = libraryNoticeIterator.next();
                Notice notice = dbLibraryNotices.get(libraryNotice.getId());

                // 새로 받아온 공지가 db에 없거나, db의 updatedDate와 달라졌다면, 새로 삽입해야한다.
                // TODO: 공지를 UPDATE하는 것과 DELETE -> INSERT 하는 방법 중 어떤것이 성능 상 좋을지 고려해볼 필요가 있음.
                if(notice == null || !notice.getUpdatedDate().equals(libraryNotice.getLastUpdated())) {
                    log.info("Library notice insert. articleId = {}, postedDate = {}, subject = {}", libraryNotice.getId(), libraryNotice.getDateCreated(), libraryNotice.getTitle());
                    newLibraryNotices.add(libraryNotice.toEntity(libraryCategory));
                } else {
                    libraryNoticeIterator.remove();
                    dbLibraryNotices.remove(libraryNotice.getId());
                }
            }
        }

        Collection<Notice> removedLibraryNotices = dbLibraryNotices.values();
        noticeRepository.deleteAll(removedLibraryNotices);
        noticeRepository.saveAll(newLibraryNotices);
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

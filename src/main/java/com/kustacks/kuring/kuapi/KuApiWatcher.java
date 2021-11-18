package com.kustacks.kuring.kuapi;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.controller.dto.StaffDTO;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.feedback.FeedbackRepository;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.notice.NoticeRepository;
import com.kustacks.kuring.domain.staff.Staff;
import com.kustacks.kuring.domain.staff.StaffRepository;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user.UserRepository;
import com.kustacks.kuring.domain.user_category.UserCategory;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.request.KuisLoginRequestBody;
import com.kustacks.kuring.kuapi.request.KuisRequestBody;
import com.kustacks.kuring.kuapi.request.notice.*;
import com.kustacks.kuring.kuapi.response.*;
import com.kustacks.kuring.kuapi.scrap.StaffScraper;
import com.kustacks.kuring.kuapi.scrap.deptinfo.StaffDeptInfo;
import com.kustacks.kuring.service.FirebaseService;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private final FirebaseService firebaseService;

    private final KuisLoginRequestBody kuisLoginRequestBody;
    private final Map<CategoryName, KuisNoticeRequestBody> noticeRequestBodies;

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    private final StaffScraper staffScraper;
    private final List<StaffDeptInfo> deptInfos;

    private final int STAFF_UPDATE_RETRY_PERIOD = 1000 * 60; // 1분후에 실패한 크론잡 재시도
//    private final int STAFF_UPDATE_RETRY_PERIOD = 1000 * 10;

    private final int MAX_KU_LOGIN_TRY = 3;
    private final int LOGIN_RETRY_PERIOD = 1000 * 10;

    private String cookieValue = "";
    private Map<String, Category> categoryMap;

    public KuApiWatcher(
            FirebaseService firebaseService,
            KuisLoginRequestBody kuisLoginRequestBody,

            BachelorKuisNoticeRequestBody bachelorNoticeRequestBody,
            ScholarshipKuisNoticeRequestBody scholarshipNoticeRequestBody,
            EmploymentKuisNoticeRequestBody employmentKuisNoticeRequestBody,
            NationalKuisNoticeRequestBody nationalKuisNoticeRequestBody,
            StudentKuisNoticeRequestBody studentKuisNoticeRequestBody,
            IndustryUnivKuisNoticeRequestBody industryUnivKuisNoticeRequestBody,
            NormalKuisNoticeRequestBody normalKuisNoticeRequestBody,

            NoticeRepository noticeRepository,
            CategoryRepository categoryRepository,
            StaffRepository staffRepository,
            UserRepository userRepository,

            StaffScraper staffScraper,
            List<StaffDeptInfo> deptInfos
    ) {
        this.firebaseService = firebaseService;

        this.kuisLoginRequestBody = kuisLoginRequestBody;

        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;

        this.staffScraper = staffScraper;
        this.deptInfos = deptInfos;

        noticeRequestBodies = new HashMap<>();
        noticeRequestBodies.put(CategoryName.BACHELOR, bachelorNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.SCHOLARSHIP, scholarshipNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.EMPLOYMENT, employmentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NATIONAL, nationalKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.STUDENT, studentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.INDUSTRY_UNIV, industryUnivKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NORMAL, normalKuisNoticeRequestBody);
    }

    /*
        서버 구동 시 처음 실행되는 상황을 고려해보면
        1. 서비스 시작 전에 서버가 처음 초기화되는 상황
            -> 등록된 FCM 토큰이 없으므로 상관없음
           
        2. 서비스 중간에 코드 수정이 있었고, 이를 반영하기 위해 prod 서버에 배포하는 상황
            -> 공지 데이터는 DB에 그대로 있을 것이고, 서버 재배포 중 새로히 추가된 공지만 알림이 갈 것이므로 문제없음

        즉, isInit 플래그는 필요 없다.
     */

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES, zone = "Asia/Seoul")
    public void watchAndUpdateNotice() {

        log.info("========== 공지 업데이트 시작 ==========");

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
        ResponseEntity<String> loginResponse = null; // 응답 제대로 받지 못하면 아래 로직에서 처리하므로, 세션 업데이트에서 null일 확률은 없음

        int kuLoginTryCount = 0;
        while(kuLoginTryCount < MAX_KU_LOGIN_TRY) {
            ++kuLoginTryCount;
            try {
                loginResponse = restTemplate.exchange(loginUrl, HttpMethod.POST, loginRequestEntity, String.class);
                break;
            } catch(RestClientException e) {
                log.error("[KuLoginException] 건국대학교 KUIS 로그인 시도가 {}회 실패했습니다.", kuLoginTryCount);
                if(kuLoginTryCount == MAX_KU_LOGIN_TRY) {
                    Sentry.captureException(new InternalLogicException(ErrorCode.KU_LOGIN_CANNOT_LOGIN, e));
                    return;
                }

                try {
                    Thread.sleep(LOGIN_RETRY_PERIOD);
                } catch(InterruptedException ex) {
                    log.warn("[KuLoginException] 건국대학교 KUIS 로그인 재시도 대기 쓰레드 sleep에 문제가 생겼습니다. 즉시 재시도합니다.");
                    Sentry.captureException(ex);
                }
            }
        }

        // 세션 업데이트
        try {
            updateSession(loginResponse);
        } catch(InternalLogicException e) {
            log.error("[KuLoginException] KUIS 로그인 응답 메세지를 파싱하는 중 오류가 발생했습니다.");
            Sentry.captureException(e);
            return;
        }


        // 공지 요청 헤더
        HttpHeaders noticeRequestHeader = createNoticeRequestHeader();

        // 공지 요청
        Map<String, KuisNoticeResponseBody> kuisNoticeResponseBodies = new LinkedHashMap<>(); // 수신한 공지 데이터를 저장할 변수
        for (CategoryName noticeCategory : noticeRequestBodies.keySet()) {
            String categoryName = noticeCategory.getName();

            KuisNoticeRequestBody kuisNoticeRequestBody = noticeRequestBodies.get(noticeCategory);

            String encodedNoticeRequestBody = KuisRequestBody.toUrlEncodedString(kuisNoticeRequestBody);
            HttpEntity<String> noticeRequestEntity = new HttpEntity<>(encodedNoticeRequestBody, noticeRequestHeader);
            ResponseEntity<KuisNoticeResponseBody> noticeResponse = restTemplate.exchange(noticeUrl, HttpMethod.POST, noticeRequestEntity, KuisNoticeResponseBody.class);

            KuisNoticeResponseBody body = noticeResponse.getBody();
            if(body == null) {
                log.error("{} 공지 요청에 대한 응답의 body가 없습니다.", noticeCategory.getName());
                Sentry.captureException(new InternalLogicException(ErrorCode.KU_NOTICE_CANNOT_PARSE_JSON));
            } else {
                kuisNoticeResponseBodies.put(categoryName, body);
            }
        }

        // DB에 있는 공지 데이터 카테고리별로 꺼내와서
        // kuisNoticeResponseBody에 있는 데이터가 DB에는 없는 경우 -> DB에 공지 추가
        // DB에 있는 데이터가 kuisNoticeResponseBody에는 없는 경우 -> DB에 공지 삭제
        List<Notice> willBeNotiNotices = updateNotice(kuisNoticeResponseBodies);

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
                log.error("도서관 공지 {}번째 요청에 대한 응답의 body가 없습니다.", reqIdx + 1);
                Sentry.captureException(new InternalLogicException(ErrorCode.LIB_CANNOT_PARSE_JSON));
                return;
            }

            boolean isLibraryRequestSuccess = libraryNoticeResponseBody.isSuccess();
            if(!isLibraryRequestSuccess) {
                log.error("도서관 공지 {}번째 요청에 대한 응답이 fail입니다.", reqIdx + 1);
                Sentry.captureException(new InternalLogicException(ErrorCode.LIB_BAD_RESPONSE));
                return;
            }

            offset = max;
            max = libraryNoticeResponseBody.getData().getTotalCount() - max;

            libraryNoticeResponseBodies.add(libraryNoticeResponseBody);
            ++reqIdx;
        }

        List<Notice> willBeNotiLibraryNotices = updateLibrary(libraryNoticeResponseBodies);
        willBeNotiNotices.addAll(willBeNotiLibraryNotices);

        List<NoticeDTO> willBeNotiNoticeDTOList = new ArrayList<>(willBeNotiNotices.size());

        int idx = 0;
        for (Notice notice : willBeNotiNotices) {
            willBeNotiNoticeDTOList.add(idx++, NoticeDTO.builder()
                    .articleId(notice.getArticleId())
                    .postedDate(notice.getPostedDate())
                    .subject(notice.getSubject())
                    .categoryName(notice.getCategory().getName())
                    .build());
        }

        // FCM으로 새롭게 수신한 공지 데이터 전송
        try {
            firebaseService.sendMessage(willBeNotiNoticeDTOList);
            log.info("FCM에 정상적으로 메세지를 전송했습니다.");
            log.info("전송된 공지 목록은 다음과 같습니다.");
            for (NoticeDTO noticeDTO : willBeNotiNoticeDTOList) {
                log.info("아이디 = {}, 날짜 = {}, 카테고리 = {}, 제목 = {}", noticeDTO.getArticleId(), noticeDTO.getPostedDate(), noticeDTO.getCategoryName(), noticeDTO.getSubject());
            }
        } catch(FirebaseMessagingException e) {
            log.error("새로운 공지의 FCM 전송에 실패했습니다.");
            Sentry.captureException(new InternalLogicException(ErrorCode.FB_FAIL_SEND, e));
        } catch(Exception e) {
            log.error("새로운 공지를 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.");
            Sentry.captureException(new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e));
        }

        log.info("========== 공지 업데이트 종료 ==========");
    }

    private List<Notice> updateLibrary(List<LibraryNoticeResponseBody> libraryNoticeResponseBodies) {

        Map<String, Notice> dbLibraryNotices = noticeRepository.findByCategoryMap(categoryMap.get(CategoryName.LIBRARY.getName()));
        List<Notice> newLibraryNotices = new LinkedList<>();

        for (LibraryNoticeResponseBody libraryNoticeResponseBody : libraryNoticeResponseBodies) {
            LibraryDataDTO data = libraryNoticeResponseBody.getData();
            List<LibraryNoticeDTO> libraryNoticeDTOList = data.getList();
            Iterator<LibraryNoticeDTO> libraryNoticeIterator = libraryNoticeDTOList.iterator();

            Category libraryCategory = categoryMap.get(CategoryName.LIBRARY.getName());

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

        return newLibraryNotices;
    }

    private List<Notice> updateNotice(Map<String, KuisNoticeResponseBody> kuisNoticeResponseBodies) {
        if(categoryMap == null) {
            categoryMap = categoryRepository.findAllMap();
        }

        List<Notice> willBeNotiNotices = new LinkedList<>();
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

            willBeNotiNotices.addAll(newNotices);
        }

        return willBeNotiNotices;
    }

    private void updateSession(ResponseEntity<String> loginResponse) {

        String body = loginResponse.getBody();

        if(body == null) {
            throw new InternalLogicException(ErrorCode.KU_LOGIN_NO_RESPONSE_BODY);
        } else {
            if(body.contains("success")) {
                HttpHeaders responseHeaders = loginResponse.getHeaders();

                if(responseHeaders.containsKey("Set-Cookie")) {
                    List<String> setCookieValues = responseHeaders.get("Set-Cookie");

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



    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS, zone = "Asia/Seoul")
    public void watchAndUpdateStaff() {

        log.info("========== 교직원 업데이트 시작 ==========");

        // www.konkuk.ac.kr 에서 스크래핑하므로, kuis 로그인 필요 없음


        /*
           각 학과별 url로 스크래핑, 교수진 데이터 수집

           스크래핑 실패한 학과들을 재시도하기 위해 호출된 경우
           values에 StaffDeptInfo 전체 값이 아닌, 매개변수로 들어온 값을 전달한다.
         */

        List<StaffDeptInfo> failDepts = new LinkedList<>();
        List<String> successDeptNames = new LinkedList<>();
        Map<String, StaffDTO> kuStaffDTOMap = new HashMap<>();

        for (StaffDeptInfo deptInfo : deptInfos) {

            try {

                scrapDeptAndConvertToMap(kuStaffDTOMap, deptInfo);
                successDeptNames.add(deptInfo.getDeptName());
            } catch(IOException | IndexOutOfBoundsException | InternalLogicException e) {

                log.error("[ScraperException] 스크래핑 중 문제가 발생했습니다.", e);
                log.error("[ScraperException] 문제가 발생한 학과 = {}", deptInfo.getDeptName());
                failDepts.add(deptInfo);
            }
        }

        updateStaff(kuStaffDTOMap, successDeptNames);

        if(failDepts.size() > 0) {
            retryStaffUpdateAfterSomeTimes(failDepts);
        } else {
            log.info("교직원 정보 업데이트 실패 없이 완료");
        }

        log.info("========== 교직원 업데이트 종료 ==========");
    }

    private void retryStaffUpdateAfterSomeTimes(List<StaffDeptInfo> failDepts) {

        try {
            Thread.sleep(STAFF_UPDATE_RETRY_PERIOD);
        } catch(InterruptedException e) {
            log.warn("[RetryStaffUpdate] 스크래핑 재시도 쓰레드 sleep 오류. 스크래핑 재시도를 즉시 시작합니다.");
        }

        log.info("[RetryStaffUpdate] 교직원 업데이트를 재시도합니다.");
        log.info("[RetryStaffUpdate] 재시도 대상 = {}", failDepts);

        List<StaffDeptInfo> retryFailDepts = retryStaffUpdate(failDepts);

        if(retryFailDepts.size() > 0) {

            log.error("[RetryStaffUpdate] 교직원 업데이트 재시도에 실패한 학과가 존재합니다.");
            log.error("[RetryStaffUpdate] 재시도 실패 학과 = {}", retryFailDepts);
        } else {
            log.info("[RetryStaffUpdate] 교직원 업데이트 재시도가 성공했습니다.");
        }
    }

    private void updateStaff(Map<String, StaffDTO> kuStaffDTOMap, List<String> successDeptNames) {
        // 스크래핑으로 수집한 교직원 정보와 비교
        // 달라진 정보가 있거나, 새로운 교직원 정보이면 db에 추가할 리스트에 저장
        // db에는 있으나 스크래핑한 리스트에 없는 교직원이라면, 삭제할 리스트에 저장

        // db에 저장되어있는 교직원 정보 조회
        Map<String, Staff> dbStaffMap = staffRepository.findByDeptContainingMap(successDeptNames);
        List<Staff> toBeUpdateStaffs = new LinkedList<>();
        Iterator<StaffDTO> kuStaffDTOIterator = kuStaffDTOMap.values().iterator();
        while(kuStaffDTOIterator.hasNext()) {
            StaffDTO staffDTO = kuStaffDTOIterator.next();

            Staff staff = dbStaffMap.get(staffDTO.getEmail());
            if(staff != null) {
                StaffDTO dbStaffDTO = StaffDTO.entityToDTO(staff);

                if(!staffDTO.equals(dbStaffDTO)) {
                    updateStaffEntity(staffDTO, staff);
                    toBeUpdateStaffs.add(staff);
                }

                dbStaffMap.remove(staffDTO.getEmail());
                kuStaffDTOIterator.remove();
            }
        }
        
        log.info("=== 삭제할 교직원 리스트 ===");
        for (String key : dbStaffMap.keySet()) {
            log.info("{} {} {}", dbStaffMap.get(key).getCollege(), dbStaffMap.get(key).getDept(), dbStaffMap.get(key).getName());
        }
        log.info("=== 업데이트할 교직원 리스트 ===");
        for (Staff toBeUpdateStaff : toBeUpdateStaffs) {
            log.info("{} {} {}", toBeUpdateStaff.getCollege(), toBeUpdateStaff.getDept(), toBeUpdateStaff.getName());
        }
        log.info("=== 추가할 교직원 리스트 ===");
        for (String key : kuStaffDTOMap.keySet()) {
            log.info("{} {} {}", kuStaffDTOMap.get(key).getCollegeName(), kuStaffDTOMap.get(key).getDeptName(), kuStaffDTOMap.get(key).getName());
        }

        staffRepository.deleteAll(dbStaffMap.values());
        staffRepository.saveAll(kuStaffDTOMap.values().stream().map(StaffDTO::toEntity).collect(Collectors.toList()));
        staffRepository.saveAll(toBeUpdateStaffs);
    }

    private void scrapDeptAndConvertToMap(Map<String, StaffDTO> kuStaffDTOMap, StaffDeptInfo deptInfo) throws IOException {

        List<StaffDTO> scrapedStaffDTOList = staffScraper.getStaffInfo(deptInfo);

        for (StaffDTO staffDTO : scrapedStaffDTOList) {
            StaffDTO mapStaffDTO = kuStaffDTOMap.get(staffDTO.getEmail());
            if(mapStaffDTO == null) {
                kuStaffDTOMap.put(staffDTO.getEmail(), staffDTO);
            } else {
                mapStaffDTO.setDeptName(mapStaffDTO.getDeptName() + ", " + staffDTO.getDeptName());
            }
        }
    }

    private List<StaffDeptInfo> retryStaffUpdate(List<StaffDeptInfo> failDepts) {

        Map<String, StaffDTO> kuStaffDTOMap = new HashMap<>();
        List<StaffDeptInfo> retryFailDepts = new LinkedList<>();
        List<StaffDeptInfo> successDepts = new LinkedList<>();

        for (StaffDeptInfo failDept : failDepts) {
            try {
                scrapDeptAndConvertToMap(kuStaffDTOMap, failDept);
                successDepts.add(failDept);
            } catch(IOException | IndexOutOfBoundsException | InternalLogicException e) {
                retryFailDepts.add(failDept);
            }
        }

        updateStaff(kuStaffDTOMap, successDepts.stream().map(StaffDeptInfo::getDeptName).collect(Collectors.toList()));

        return retryFailDepts;
    }

    private void updateStaffEntity(StaffDTO staffDTO, Staff staff) {
        staff.setName(staffDTO.getName());
        staff.setMajor(staffDTO.getMajor());
        staff.setLab(staffDTO.getLab());
        staff.setPhone(staffDTO.getPhone());
        staff.setEmail(staffDTO.getEmail());
        staff.setDept(staffDTO.getDeptName());
        staff.setCollege(staffDTO.getCollegeName());
    }




//    @Scheduled(cron = "0/10 * * * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "0 30 0 1 * ?", zone = "Asia/Seoul")
    public void verifyFCMTokens() {

        log.info("========== 토큰 유효성 필터링 시작 ==========");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            try {
                firebaseService.verifyToken(user.getToken());
            } catch(FirebaseMessagingException e) {

                for (UserCategory userCategory : user.getUserCategories()) {
                    try {
                        firebaseService.unsubscribe(user.getToken(), userCategory.getCategory().getName());
                    } catch (FirebaseMessagingException | InternalLogicException ex) {
                        log.error("유효하지 않은 토큰의 구독 해제 중 오류가 발생했습니다.");
                        log.error("토큰 = {}, 카테고리 = {}", user.getToken(), userCategory.getCategory().getName());
                        Sentry.captureException(new InternalLogicException(ErrorCode.FB_FAIL_UNSUBSCRIBE, ex));
                    }
                }

                userRepository.delete(user);
            }
        }

        log.info("========== 토큰 유효성 필터링 종료 ==========");
    }
}

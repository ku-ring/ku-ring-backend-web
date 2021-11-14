package com.kustacks.kuring.kuapi;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.controller.dto.StaffDTO;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.notice.NoticeRepository;
import com.kustacks.kuring.domain.staff.Staff;
import com.kustacks.kuring.domain.staff.StaffRepository;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.request.*;
import com.kustacks.kuring.kuapi.request.notice.*;
import com.kustacks.kuring.kuapi.response.KuisNoticeDTO;
import com.kustacks.kuring.kuapi.response.KuisNoticeResponseBody;
import com.kustacks.kuring.kuapi.response.LibraryNoticeDTO;
import com.kustacks.kuring.kuapi.response.LibraryNoticeResponseBody;
import com.kustacks.kuring.kuapi.response.LibraryDataDTO;
import com.kustacks.kuring.service.FirebaseService;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
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

    private final StaffScraper staffScraper;
    private final ThreadPoolTaskExecutor executor;
    
    private final int STAFF_UPDATE_RETRY_TIME = 1000 * 60; // 1분후에 실패한 크론잡 재시도
//    private final int STAFF_UPDATE_RETRY_TIME = 1000 * 10;

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

            StaffScraper staffScraper,
            ThreadPoolTaskExecutor executor
    ) {
        this.firebaseService = firebaseService;

        this.kuisLoginRequestBody = kuisLoginRequestBody;

        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
        this.staffRepository = staffRepository;

        this.staffScraper = staffScraper;
        this.executor = executor;

        noticeRequestBodies = new HashMap<>();
        noticeRequestBodies.put(CategoryName.BACHELOR, bachelorNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.SCHOLARSHIP, scholarshipNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.EMPLOYMENT, employmentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NATIONAL, nationalKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.STUDENT, studentKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.INDUSTRY_UNIV, industryUnivKuisNoticeRequestBody);
        noticeRequestBodies.put(CategoryName.NORMAL, normalKuisNoticeRequestBody);
    }

    // TODO: 서버 시작할 때 실행되는 경우, FCM 알림 보내지 않게 설정하기
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void noticeCronJob() {
        watchAndUpdateNotice(false);
    }

    public void watchAndUpdateNotice(boolean isInit) {

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
        for (CategoryName noticeCategory : noticeRequestBodies.keySet()) {
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
        
        // 서버가 처음 실행될 땐 FCM에 메세지 보내지 않아야됨
        if(isInit) {
            return;
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
            throw new InternalLogicException(ErrorCode.FB_FAIL_SEND, e);
        } catch(Exception e) {
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e);
        }
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




    // TODO: 현재 건국대학교 서버 불안정으로 인해 교직원 정보가 제대로 안뜨는 중. 이에 1시간마다 갱신하도록 임시 수정해 둠
    @Scheduled(cron = "@monthly", zone = "Asia/Seoul")
    public void watchAndUpdateStaff() {

        // www.konkuk.ac.kr 에서 스크래핑하므로, kuis 로그인 필요 없음


        /*
           각 학과별 url로 스크래핑, 교수진 데이터 수집

           스크래핑 실패한 학과들을 재시도하기 위해 호출된 경우
           values에 StaffDeptInfo 전체 값이 아닌, 매개변수로 들어온 값을 전달한다.
         */

        List<StaffDeptInfo> failDepts = new LinkedList<>();
        List<String> successDeptNames = new LinkedList<>();
        Map<String, StaffDTO> kuStaffDTOMap = new HashMap<>();
        StaffDeptInfo[] values = StaffDeptInfo.values();

        for (StaffDeptInfo value : values) {
            
            // 일단 행정실 Enum은 무시하도록 함
            if(value.getUrl() == null) {
                continue;
            }

            try {

                scrapDeptAndConvertToMap(kuStaffDTOMap, value);
                successDeptNames.add(value.getName());
                log.info("{} 스크래핑 완료", value.getName());
            } catch(IOException | IndexOutOfBoundsException e) {

                log.error("[ScraperException] 스크래핑 중 문제가 발생했습니다.");
                log.error("[ScraperException] 문제가 발생한 학과 = {}", value.getName());
                failDepts.add(value);
            }
        }

        updateStaff(kuStaffDTOMap, successDeptNames);

        if(failDepts.size() > 0) {
            retryStaffUpdateAfterSomeTimes(failDepts);
        } else {
            log.info("교직원 정보 업데이트 실패 없이 완료");
        }
    }

    private void retryStaffUpdateAfterSomeTimes(List<StaffDeptInfo> failDepts) {
        executor.execute(() -> {

            try {

                Thread.sleep(STAFF_UPDATE_RETRY_TIME);

                log.info("[ThreadExecutor] 교직원 업데이트를 재시도합니다.");
                log.info("[ThreadExecutor] 재시도 대상 = {}", failDepts);
                retryStaffUpdate(failDepts);
                log.info("[ThreadExecutor] 교직원 업데이트 재시도가 성공했습니다.");
            } catch(IOException | InternalLogicException | InterruptedException e) {
                log.error("[ScraperException] 스크래핑 재시도 중 문제가 발생했습니다.", e);
                Sentry.captureException(e);
            }
        });
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

    private void scrapDeptAndConvertToMap(Map<String, StaffDTO> kuStaffDTOMap, StaffDeptInfo value) throws IOException {

        List<StaffDTO> scrapedStaffDTOList = staffScraper.getStaffInfo(value);
        for (StaffDTO staffDTO : scrapedStaffDTOList) {
            StaffDTO mapStaffDTO = kuStaffDTOMap.get(staffDTO.getEmail());
            if(mapStaffDTO == null) {
                kuStaffDTOMap.put(staffDTO.getEmail(), staffDTO);
            } else {
                mapStaffDTO.setDeptName(mapStaffDTO.getDeptName() + ", " + staffDTO.getDeptName());
            }
        }
    }

    private void retryStaffUpdate(List<StaffDeptInfo> failDepts) throws IOException {

        Map<String, StaffDTO> kuStaffDTOMap = new HashMap<>();

        for (StaffDeptInfo failDept : failDepts) {
            scrapDeptAndConvertToMap(kuStaffDTOMap, failDept);
        }

        updateStaff(kuStaffDTOMap, failDepts.stream().map(StaffDeptInfo::getName).collect(Collectors.toList()));
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
}

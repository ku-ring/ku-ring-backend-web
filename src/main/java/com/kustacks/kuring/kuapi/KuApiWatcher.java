package com.kustacks.kuring.kuapi;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.StaffDTO;
import com.kustacks.kuring.domain.staff.Staff;
import com.kustacks.kuring.domain.staff.StaffRepository;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user.UserRepository;
import com.kustacks.kuring.domain.user_category.UserCategoryRepository;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.scrap.StaffScraper;
import com.kustacks.kuring.kuapi.scrap.deptinfo.StaffDeptInfo;
import com.kustacks.kuring.service.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KuApiWatcher {

    private final FirebaseService firebaseService;

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;

    private final StaffScraper staffScraper;
    private final List<StaffDeptInfo> deptInfos;

    private final int STAFF_UPDATE_RETRY_PERIOD = 1000 * 60; // 1분후에 실패한 크론잡 재시도
//    private final int STAFF_UPDATE_RETRY_PERIOD = 1000 * 10;

    public KuApiWatcher(
            FirebaseService firebaseService,

            StaffRepository staffRepository,
            UserRepository userRepository,
            UserCategoryRepository userCategoryRepository,

            StaffScraper staffScraper,
            List<StaffDeptInfo> deptInfos
    ) {

        this.firebaseService = firebaseService;

        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.userCategoryRepository = userCategoryRepository;

        this.staffScraper = staffScraper;
        this.deptInfos = deptInfos;
    }

    /*
        서버 구동 시 처음 실행되는 상황을 고려해보면
        1. 서비스 시작 전에 서버가 처음 초기화되는 상황
            -> 등록된 FCM 토큰이 없으므로 상관없음
           
        2. 서비스 중간에 코드 수정이 있었고, 이를 반영하기 위해 prod 서버에 배포하는 상황
            -> 공지 데이터는 DB에 그대로 있을 것이고, 서버 재배포 중 새로히 추가된 공지만 알림이 갈 것이므로 문제없음

        즉, isInit 플래그는 필요 없다.
     */

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
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



    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    public void verifyFCMTokens() {

        log.info("========== 토큰 유효성 필터링 시작 ==========");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            String token = user.getToken();
            try {
                firebaseService.verifyToken(token);
            } catch(FirebaseMessagingException e) {
                userCategoryRepository.deleteAll(user.getUserCategories());
                userRepository.deleteByToken(token);
                log.info("삭제한 토큰 = {}", token);
            }
        }

        log.info("========== 토큰 유효성 필터링 종료 ==========");
    }
}

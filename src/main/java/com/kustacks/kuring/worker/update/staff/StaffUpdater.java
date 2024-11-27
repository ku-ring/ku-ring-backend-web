package com.kustacks.kuring.worker.update.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.StaffScraper;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import com.kustacks.kuring.worker.update.staff.dto.StaffScrapResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffUpdater {

    private final StaffDataSynchronizer staffDataSynchronizer;
    private final StaffScraper staffScraper;
    private final List<DeptInfo> deptInfos;

    /*
       각 학과별 url로 스크래핑, 교수진 데이터 수집
       스크래핑 실패한 학과들을 재시도하기 위해 호출된 경우
       values에 StaffDeptInfo 전체 값이 아닌, 매개변수로 들어온 값을 전달한다.
     */
    //@Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    @Deprecated(since = "2.7.3", forRemoval = true)
    public void update() {
        log.info("========== 교직원 업데이트 시작 ==========");

        StaffScrapResults scrapResults = scrapAllDepartmentsStaffs();
        staffDataSynchronizer.compareAndUpdateDb(scrapResults);

        log.info("========== 교직원 업데이트 종료 ==========");
    }

    private StaffScrapResults scrapAllDepartmentsStaffs() {
        Map<String, StaffDto> kuStaffDtoMap = new HashMap<>();
        List<String> successDepartmentNames = new LinkedList<>();

        deptInfos.stream()
                .filter(DeptInfo::isSupportStaffScrap)
                .forEach(deptInfo -> {
                    scrapSingleDepartmentsStaffs(kuStaffDtoMap, successDepartmentNames, deptInfo);
                });
        return new StaffScrapResults(kuStaffDtoMap, successDepartmentNames);
    }

    private void scrapSingleDepartmentsStaffs(Map<String, StaffDto> kuStaffDtoMap, List<String> successDepartmentNames, DeptInfo deptInfo) {
        try {
            Map<String, StaffDto> staffScrapResultMap = scrapStaffByDepartment(deptInfo);
            mergeForMultipleDepartmentsStaff(kuStaffDtoMap, staffScrapResultMap);
            successDepartmentNames.add(deptInfo.getDeptName());
        } catch (InternalLogicException e) {
            log.error("[StaffScraperException] {}학과 교직원 스크래핑 문제 발생.", deptInfo.getDeptName());
        }
    }

    private Map<String, StaffDto> scrapStaffByDepartment(DeptInfo deptInfo) {
        List<StaffDto> scrapedStaffDtos = staffScraper.scrap(deptInfo);
        return convertStaffDtoMap(scrapedStaffDtos);
    }

    private Map<String, StaffDto> convertStaffDtoMap(List<StaffDto> scrapedStaffDtos) {
        return scrapedStaffDtos.stream()
                .collect(Collectors.toMap(StaffDto::getEmail, staffDto -> staffDto));
    }

    private void mergeForMultipleDepartmentsStaff(
            Map<String, StaffDto> kuStaffDTOMap,
            Map<String, StaffDto> staffDtoMap
    ) {
        staffDtoMap.forEach((key, value) -> kuStaffDTOMap.merge(key, value, (v1, v2) -> {
                    v1.setDeptName(v1.getDeptName() + ", " + v2.getDeptName());
                    return v1;
                }
        ));
    }
}

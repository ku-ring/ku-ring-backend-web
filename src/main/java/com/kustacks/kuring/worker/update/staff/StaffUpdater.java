package com.kustacks.kuring.worker.update.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import com.kustacks.kuring.worker.scrap.StaffScraper;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.update.staff.dto.StaffDto;
import com.kustacks.kuring.worker.update.staff.dto.StaffScrapResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffUpdater {

    private final StaffDataSynchronizer staffDataSynchronizer;
    private final StaffScraper staffScraper;
    private final List<DeptInfo> deptInfos;
    private final FeatureFlags featureFlags;

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
//    @Scheduled(cron = "0 0/5 * * * *")
    public void update() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_STAFF.getFeature())) {
            log.info("========== 교직원 업데이트 시작 ==========");

            StaffScrapResults scrapResults = scrapAllDepartmentsStaffs();
            staffDataSynchronizer.compareAndUpdateDb(scrapResults);

            log.info("========== 교직원 업데이트 종료 ==========");
        }
    }

    private StaffScrapResults scrapAllDepartmentsStaffs() {
        Map<String, StaffDto> kuStaffDtoMap = new HashMap<>();

        deptInfos.stream()
                .filter(DeptInfo::isSupportStaffScrap)
                .forEach(deptInfo -> scrapSingleDepartmentsStaffs(kuStaffDtoMap, deptInfo));
        return new StaffScrapResults(kuStaffDtoMap);
    }

    private void scrapSingleDepartmentsStaffs(Map<String, StaffDto> kuStaffDtoMap, DeptInfo deptInfo) {
        try {
            Map<String, StaffDto> staffScrapResultMap = scrapStaffByDepartment(deptInfo);
            mergeForMultipleDepartmentsStaff(kuStaffDtoMap, staffScrapResultMap);
            log.info("스크랩 완료 {} ", deptInfo.getDeptName());
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
                .collect(Collectors.toMap(
                        StaffDto::identifier,
                        staffDto -> staffDto,
                        (existing, replacement) -> existing));
    }

    private void mergeForMultipleDepartmentsStaff(
            Map<String, StaffDto> kuStaffDTOMap,
            Map<String, StaffDto> staffDtoMap
    ) {
        staffDtoMap.forEach((key, value) -> kuStaffDTOMap.merge(key, value,
                (v1, v2) -> {
                    v1.setDeptName(v1.getDeptName() + ", " + v2.getDeptName());
                    return v1;
                }
        ));
    }
}

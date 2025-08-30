package com.kustacks.kuring.worker.update.notice;


import com.kustacks.kuring.config.featureflag.FeatureFlags;
import com.kustacks.kuring.config.featureflag.KuringFeatures;
import com.kustacks.kuring.message.application.service.FirebaseNotificationService;
import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.kustacks.kuring.notice.domain.DepartmentName.REAL_ESTATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentNoticeUpdater {

    private final List<DeptInfo> deptInfoList;
    private final DepartmentNoticeScraperTemplate scrapperTemplate;
    private final NoticeQueryPort noticeQueryPort;
    private final NoticeCommandPort noticeCommandPort;
    private final ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;
    private final FirebaseNotificationService notificationService;
    private final NoticeUpdateSupport noticeUpdateSupport;
    private final FeatureFlags featureFlags;

    @Scheduled(cron = "0 15/20 7-19 * * *", zone = "Asia/Seoul") // 학교 공지는 오전 7:15 ~ 오후 7:55분 사이에 20분마다 업데이트 된다.
    public void update() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_DEPARTMENT_NOTICE.getFeature())) {
            log.info("******** 학과별 최신 공지 업데이트 시작 ********");

            for (DeptInfo deptInfo : deptInfoList) {
                CompletableFuture
                        .supplyAsync(
                                () -> updateDepartmentAsync(deptInfo, DeptInfo::scrapLatestPageHtml),
                                noticeUpdaterThreadTaskExecutor
                        ).thenApply(
                                scrapResults -> compareLatestAndUpdateDB(scrapResults, deptInfo.getDeptName())
                        ).thenAccept(
                                notificationService::sendNotifications
                        );
            }
        }
    }

    @Scheduled(cron = "0 0 23 * * 5", zone = "Asia/Seoul") // 전체 업데이트는 매주 금요일 오후 11시에 한다.
    public void updateAll() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_DEPARTMENT_NOTICE.getFeature())) {
            log.info("******** 학과별 전체 공지 업데이트 시작 ********");

            for (DeptInfo deptInfo : deptInfoList) {
                if (deptInfo.isSameDepartment(REAL_ESTATE)) {
                    continue;
                }

                CompletableFuture
                        .supplyAsync(() -> updateDepartmentAsync(deptInfo, DeptInfo::scrapAllPageHtml), noticeUpdaterThreadTaskExecutor)
                        .thenAccept(scrapResults -> compareAllAndUpdateDB(scrapResults, deptInfo.getDeptName()));
            }
        }
    }

    private List<ComplexNoticeFormatDto> updateDepartmentAsync(DeptInfo deptInfo, Function<DeptInfo, List<ScrapingResultDto>> decisionMaker) {
        return scrapperTemplate.scrap(deptInfo, decisionMaker);
    }

    private List<DepartmentNotice> compareLatestAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, String departmentName) {
        DepartmentName departmentNameEnum = DepartmentName.fromKor(departmentName);

        List<DepartmentNotice> newNoticeList = new ArrayList<>();
        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 모든 중요 공지를 가져와서
            List<Integer> savedImportantArticleIds = noticeQueryPort.findImportantArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            List<DepartmentNotice> newImportantNotices = saveNewNotices(scrapResult.getImportantNoticeList(), savedImportantArticleIds, departmentNameEnum, true);
            newNoticeList.addAll(newImportantNotices);

            // DB에서 모든 일반 공지 id를 가져와서
            List<Integer> savedNormalArticleIds = noticeQueryPort.findNormalArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            List<DepartmentNotice> newNormalNotices = saveNewNotices(scrapResult.getNormalNoticeList(), savedNormalArticleIds, departmentNameEnum, false);
            newNoticeList.addAll(newNormalNotices);
        }

        return newNoticeList;
    }

    private List<DepartmentNotice> saveNewNotices(List<CommonNoticeFormatDto> scrapResults, List<Integer> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = noticeUpdateSupport.filteringSoonSaveDepartmentNotices(scrapResults, savedArticleIds, departmentNameEnum, important);
        noticeCommandPort.saveAllDepartmentNotices(newNotices);
        return newNotices;
    }

    private void compareAllAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, String departmentName) {
        if (scrapResults.isEmpty()) {
            return;
        }

        DepartmentName departmentNameEnum = DepartmentName.fromKor(departmentName);

        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 최신 중요 공지를 가져와서
            List<Integer> savedImportantArticleIds = noticeQueryPort.findImportantArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getImportantNoticeList(), savedImportantArticleIds, departmentNameEnum, true);

            // DB에서 모든 일반 공지의 id를 가져와서
            List<Integer> savedNormalArticleIds = noticeQueryPort.findNormalArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getNormalNoticeList(), savedNormalArticleIds, departmentNameEnum, false);
        }
    }

    private void synchronizationWithDb(List<CommonNoticeFormatDto> scrapResults, List<Integer> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = noticeUpdateSupport.filteringSoonSaveDepartmentNotices(scrapResults, savedArticleIds, departmentNameEnum, important);

        List<Integer> latestNoticeIds = noticeUpdateSupport.extractDepartmentNoticeIds(scrapResults);

        List<String> deletedNoticesArticleIds = noticeUpdateSupport.filteringSoonDeleteDepartmentNoticeIds(savedArticleIds, latestNoticeIds);

        noticeCommandPort.saveAllDepartmentNotices(newNotices);

        if (!deletedNoticesArticleIds.isEmpty()) {
            noticeCommandPort.deleteAllByIdsAndDepartment(departmentNameEnum, deletedNoticesArticleIds);
        }
    }
}

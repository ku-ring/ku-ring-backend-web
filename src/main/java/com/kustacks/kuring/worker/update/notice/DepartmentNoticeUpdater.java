package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kustacks.kuring.notice.domain.DepartmentName.REAL_ESTATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentNoticeUpdater {

    private final List<DeptInfo> deptInfoList;
    private final DepartmentNoticeScraperTemplate scrapperTemplate;
    private final DepartmentNoticeRepository departmentNoticeRepository;
    private final ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;
    private final FirebaseService firebaseService;

    private static long startTime = 0L;

    @Scheduled(cron = "0 5/10 8-19 * * *", zone = "Asia/Seoul") // 학교 공지는 오전 8:10 ~ 오후 7:55분 사이에 10분마다 업데이트 된다.
    public void update() {
        log.info("******** 학과별 최신 공지 업데이트 시작 ********");
        startTime = System.currentTimeMillis();

        for (DeptInfo deptInfo : deptInfoList) {
            CompletableFuture
                    .supplyAsync(() -> updateDepartmentAsync(deptInfo, DeptInfo::scrapLatestPageHtml), noticeUpdaterThreadTaskExecutor)
                    .thenApply(scrapResults -> compareLatestAndUpdateDB(scrapResults, deptInfo.getDeptName()))
                    .thenAccept(firebaseService::sendNotificationByFcm);
        }
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul") // 전체 업데이트는 매일 오전 2시에 한다.
    public void updateAll() {
        log.info("******** 학과별 전체 공지 업데이트 시작 ********");
        startTime = System.currentTimeMillis();

        for (DeptInfo deptInfo : deptInfoList) {
            if (deptInfo.isSameDepartment(REAL_ESTATE)) {
                continue;
            }

            CompletableFuture
                    .supplyAsync(() -> updateDepartmentAsync(deptInfo, DeptInfo::scrapAllPageHtml), noticeUpdaterThreadTaskExecutor)
                    .thenAccept(scrapResults -> compareAllAndUpdateDB(scrapResults, deptInfo.getDeptName()));
        }
    }

    private List<ComplexNoticeFormatDto> updateDepartmentAsync(DeptInfo deptInfo, Function<DeptInfo, List<ScrapingResultDto>> decisionMaker) {
        List<ComplexNoticeFormatDto> scrapResults = scrapperTemplate.scrap(deptInfo, decisionMaker);

        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            scrapResult.reverseEachNoticeList();
        }

        return scrapResults;
    }

    private List<DepartmentNotice> compareLatestAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, String departmentName) {
        DepartmentName departmentNameEnum = DepartmentName.fromKor(departmentName);

        List<DepartmentNotice> newNoticeList = new ArrayList<>();
        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 모든 중요 공지를 가져와서
            List<Integer> savedImportantArticleIds = departmentNoticeRepository.findImportantArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            List<DepartmentNotice> newImportantNotices = saveNewNotices(scrapResult.getImportantNoticeList(), savedImportantArticleIds, departmentNameEnum, true);
            newNoticeList.addAll(newImportantNotices);

            // DB에서 모든 일반 공지 id를 가져와서
            List<Integer> savedNormalArticleIds = departmentNoticeRepository.findNormalArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            List<DepartmentNotice> newNormalNotices = saveNewNotices(scrapResult.getNormalNoticeList(), savedNormalArticleIds, departmentNameEnum, false);
            newNoticeList.addAll(newNormalNotices);
        }

        long endTime = System.currentTimeMillis();
        log.info("[학과] 업데이트 시작으로부터 {}millis 만큼 지남", endTime - startTime);

        return newNoticeList;
    }

    private List<DepartmentNotice> saveNewNotices(List<CommonNoticeFormatDto> scrapResults, List<Integer> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = filteringSoonSaveNotice(scrapResults, savedArticleIds, departmentNameEnum, important);
        departmentNoticeRepository.saveAllAndFlush(newNotices);
        return newNotices;
    }

    private void compareAllAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, String departmentName) {
        if (scrapResults.isEmpty()) {
            return;
        }

        DepartmentName departmentNameEnum = DepartmentName.fromKor(departmentName);

        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 최신 중요 공지를 가져와서
            List<Integer> savedImportantArticleIds = departmentNoticeRepository.findImportantArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getImportantNoticeList(), savedImportantArticleIds, departmentNameEnum, true);

            // DB에서 모든 일반 공지의 id를 가져와서
            List<Integer> savedNormalArticleIds = departmentNoticeRepository.findNormalArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getNormalNoticeList(), savedNormalArticleIds, departmentNameEnum, false);
        }

        long endTime = System.currentTimeMillis();
        log.info("[학과] 업데이트 시작으로부터 {}millis 만큼 지남", endTime - startTime);
    }

    private void synchronizationWithDb(List<CommonNoticeFormatDto> scrapResults, List<Integer> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = filteringSoonSaveNotice(scrapResults, savedArticleIds, departmentNameEnum, important);

        List<Integer> latestNoticeIds = extractIdList(scrapResults);

        List<String> deletedNoticesArticleIds = filteringSoonDeleteIds(savedArticleIds, latestNoticeIds);

        departmentNoticeRepository.saveAllAndFlush(newNotices);

        if (!deletedNoticesArticleIds.isEmpty()) {
            departmentNoticeRepository.deleteAllByIdsAndDepartment(departmentNameEnum, deletedNoticesArticleIds);
        }
    }

    private List<DepartmentNotice> filteringSoonSaveNotice(List<CommonNoticeFormatDto> scrapResults, List<Integer> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = new LinkedList<>(); // 뒤에 추가만 계속 하기 때문에 arrayList가 아닌 Linked List 사용 O(1)
        for (CommonNoticeFormatDto notice : scrapResults) {
            try {
                if (Collections.binarySearch(savedArticleIds, Integer.valueOf(notice.getArticleId())) < 0) { // 정렬되어있다, 이진탐색으로 O(logN)안에 수행
                    DepartmentNotice newDepartmentNotice = convert(notice, departmentNameEnum, CategoryName.DEPARTMENT, important);
                    newNotices.add(newDepartmentNotice);
                }
            } catch (IncorrectResultSizeDataAccessException e) {
                log.error("오류가 발생한 공지 정보");
                log.error("articleId = {}", notice.getArticleId());
                log.error("postedDate = {}", notice.getPostedDate());
                log.error("subject = {}", notice.getSubject());
            }
        }
        return newNotices;
    }

    private List<String> filteringSoonDeleteIds(List<Integer> savedArticleIds, List<Integer> latestNoticeIds) {
        return savedArticleIds.stream()
                .filter(savedArticleId -> Collections.binarySearch(latestNoticeIds, savedArticleId) < 0)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    private List<Integer> extractIdList(List<CommonNoticeFormatDto> scrapResults) {
        return scrapResults.stream()
                .map(scrap -> Integer.parseInt(scrap.getArticleId()))
                .sorted()
                .collect(Collectors.toList());
    }

    private DepartmentNotice convert(CommonNoticeFormatDto dto, DepartmentName departmentNameEnum, CategoryName categoryName, boolean important) {
        return DepartmentNotice.builder()
                .articleId(dto.getArticleId())
                .postedDate(dto.getPostedDate())
                .updatedDate(dto.getUpdatedDate())
                .subject(dto.getSubject())
                .fullUrl(dto.getFullUrl())
                .important(important)
                .categoryName(categoryName)
                .departmentName(departmentNameEnum)
                .build();
    }
}

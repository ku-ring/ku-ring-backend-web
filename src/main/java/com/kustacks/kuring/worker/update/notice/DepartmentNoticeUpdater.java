package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.update.Updater;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentNoticeUpdater implements Updater {

    private static final int TOTAL_NOTICE_COUNT_PER_PAGE = 12;

    private final DepartmentNoticeScraperTemplate scrapperTemplate;
    private final List<DeptInfo> deptInfoList;
    private final DepartmentNoticeRepository departmentNoticeRepository;
    private final ThreadPoolTaskExecutor departmentNoticeUpdaterThreadTaskExecutor;
    private final Map<String, Category> categoryMap;

    private static long startTime = 0L;

    @Override
    @Scheduled(cron = "0 10/20 8-18 * * *", zone = "Asia/Seoul") // 학교 공지는 오전 8:10 ~ 오후 6:55분 사이에 20분마다 업데이트 된다.
    public void update() {
        log.info("******** 학과별 최신 공지 업데이트 시작 ********");
        startTime = System.currentTimeMillis();

        for (DeptInfo deptInfo : deptInfoList) {
            CompletableFuture
                    .supplyAsync(() -> updateDepartmentAsync(deptInfo, DeptInfo::scrapLatestPageHtml), departmentNoticeUpdaterThreadTaskExecutor)
                    .thenAccept(scrapResults -> compareLatestAndUpdateDB(scrapResults, deptInfo.getDeptName()));
        }
    }

    @Scheduled(cron = "0 0 2,19 * * *") // 전체 업데이트는 매일 새벽 2시, 오후 7시에 진행
    public void updateAll() {
        log.info("******** 학과별 전체 공지 업데이트 시작 ********");
        startTime = System.currentTimeMillis();

        for (DeptInfo deptInfo : deptInfoList) {
            CompletableFuture
                    .supplyAsync(() -> updateDepartmentAsync(deptInfo, DeptInfo::scrapAllPageHtml), departmentNoticeUpdaterThreadTaskExecutor)
                    .thenAccept(scrapResults -> compareAllAndUpdateDB(scrapResults, deptInfo.getDeptName()));
        }
    }

    private List<ComplexNoticeFormatDto> updateDepartmentAsync(DeptInfo deptInfo, Function<DeptInfo, List<ScrapingResultDto>> decisionMaker) {
        List<ComplexNoticeFormatDto> scrapResults = scrapperTemplate.scrap(deptInfo, decisionMaker);

        // noticeAPIClient 혹은 scraper에서 새로운 공지를 감지할 때, 가장 최신에 올라온 공지를 list에 순차적으로 담는다.
        // 이 때문에 만약 같은 시간대에 감지된 두 공지가 있다면, 보다 최신 공지가 리스트의 앞 인덱스에 위치하게 되고, 이를 그대로 DB에 적용한다면
        // 보다 최신인 공지가 DB에 먼저 삽입되어, kuring API 서버에서 이를 덜 신선한 공지로 판단하게 된다.
        // 이에 commonNoticeFormatDTOList를 reverse하여 공지의 신선도 순서를 유지한다.
        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            scrapResult.reverseEachNoticeList();
        }

        return scrapResults;
    }

    private void compareLatestAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, String departmentName) {
        DepartmentName departmentNameEnum = DepartmentName.fromKor(departmentName);

        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 최신 중요 공지를 가져와서
            List<String> savedImportantArticleIds = departmentNoticeRepository.findImportantArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            saveNewNotices(scrapResult.getImportantNoticeList(), savedImportantArticleIds, departmentNameEnum, true);

            // DB에서 최신 60개의 일반 공지 id를 가져와서
            List<String> savedNormalArticleIds = departmentNoticeRepository.findNormalArticleIdsByDepartmentWithLimit(departmentNameEnum, TOTAL_NOTICE_COUNT_PER_PAGE * 5);

            // db와 싱크를 맞춘다
            saveNewNotices(scrapResult.getNormalNoticeList(), savedNormalArticleIds, departmentNameEnum, false);
        }

        long endTime = System.currentTimeMillis();
        log.info("[학과] 업데이트 시작으로부터 {}millis 만큼 지남", endTime - startTime);
    }

    private void saveNewNotices(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = filteringSoonSaveNotice(scrapResults, savedArticleIds, departmentNameEnum, important);
        departmentNoticeRepository.saveAllAndFlush(newNotices);
    }

    private void compareAllAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, String departmentName) {
        DepartmentName departmentNameEnum = DepartmentName.fromKor(departmentName);

        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 최신 중요 공지를 가져와서
            List<String> savedImportantArticleIds = departmentNoticeRepository.findImportantArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getImportantNoticeList(), savedImportantArticleIds, departmentNameEnum, true);

            // DB에서 모든 일반 공지의 id를 가져와서
            List<String> savedNormalArticleIds = departmentNoticeRepository.findNormalArticleIdsByDepartment(departmentNameEnum);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getNormalNoticeList(), savedNormalArticleIds, departmentNameEnum, false);
        }

        long endTime = System.currentTimeMillis();
        log.info("[학과] 업데이트 시작으로부터 {}millis 만큼 지남", endTime - startTime);
    }

    private void synchronizationWithDb(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = filteringSoonSaveNotice(scrapResults, savedArticleIds, departmentNameEnum, important);

        List<String> latestNoticeIds = extractIdList(scrapResults);

        List<String> deletedNoticesArticleIds = filteringSoonDeleteIds(savedArticleIds, latestNoticeIds);

        departmentNoticeRepository.saveAllAndFlush(newNotices);
        // TODO : 삭제 로직 이상함
        // TODO : articleId Long타입으로 수정
        departmentNoticeRepository.deleteAllByIdsAndDepartment(departmentNameEnum, deletedNoticesArticleIds);
    }

    private List<DepartmentNotice> filteringSoonSaveNotice(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, DepartmentName departmentNameEnum, boolean important) {
        List<DepartmentNotice> newNotices = new LinkedList<>(); // 뒤에 추가만 계속 하기 때문에 arrayList가 아닌 Linked List 사용 O(1)
        for (CommonNoticeFormatDto notice : scrapResults) {
            try {
                if (!savedArticleIds.contains(notice.getArticleId())) { // 정렬되어있다, 이진탐색으로 O(logN)안에 수행
                    Category category = categoryMap.get("department");
                    DepartmentNotice newDepartmentNotice = convert(notice, departmentNameEnum, category, important);
                    newNotices.add(newDepartmentNotice);
                }
            } catch (IncorrectResultSizeDataAccessException e) {
                log.error("오류가 발생한 공지 정보");
                log.error("articleId = {}", notice.getArticleId());
                log.error("postedDate = {}", notice.getPostedDate());
                log.error("subject = {}", notice.getSubject());
            }

//            if (Collections.binarySearch(savedArticleIds, notice.getArticleId()) < 0) { // 정렬되어있다, 이진탐색으로 O(logN)안에 수행
//                Category category = categoryMap.get("department");
//                DepartmentNotice newDepartmentNotice = convert(notice, departmentNameEnum, category);
//                newNotices.add(newDepartmentNotice);
//            }
        }
        return newNotices;
    }

    private List<String> filteringSoonDeleteIds(List<String> savedArticleIds, List<String> latestNoticeIds) {
        return savedArticleIds.stream()
                .filter(savedArticleId -> !latestNoticeIds.contains(savedArticleId))
                .collect(Collectors.toList());
    }

    private List<String> extractIdList(List<CommonNoticeFormatDto> scrapResults) {
        return scrapResults.stream()
                .map(CommonNoticeFormatDto::getArticleId)
                .collect(Collectors.toList());
    }

    private DepartmentNotice convert(CommonNoticeFormatDto dto, DepartmentName departmentNameEnum, Category category, boolean important) {
        return DepartmentNotice.builder()
                .articleId(dto.getArticleId())
                .postedDate(dto.getPostedDate())
                .updatedDate(dto.getUpdatedDate())
                .subject(dto.getSubject())
                .fullUrl(dto.getFullUrl())
                .important(important)
                .category(category)
                .departmentName(departmentNameEnum)
                .build();
    }
}

package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.update.Updater;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
    @Scheduled(cron = "0 0/20 9-18 * * *", zone = "Asia/Seoul") // 학교 공지는 오전 9시 ~ 오후 6:55분 사이에 20분마다 업데이트 된다.
    public void update() {
        log.info("******** 학과별 최신 공지 업데이트 시작 ********");
        startTime = System.currentTimeMillis();

        for (DeptInfo deptInfo : deptInfoList) {
            CompletableFuture
                    .supplyAsync(() -> updateDepartmentAsync(deptInfo, DeptInfo::scrapLatestPageHtml), departmentNoticeUpdaterThreadTaskExecutor)
                    .thenAccept(scrapResults -> compareLatestAndUpdateDB(scrapResults, deptInfo.getDeptName()));
        }
    }

    @Scheduled(cron = "0 0 2 * * *") // 전체 업데이트는 매일 새벽 2시에 진행
    public void updateAll() {
        log.info("******** 학과별 전체 공지 업데이트 시작 ********");
        startTime = System.currentTimeMillis();

        for (DeptInfo deptInfo : deptInfoList) {
            CompletableFuture
                    .supplyAsync(() -> updateDepartmentAsync(deptInfo, DeptInfo::scrapAllPageHtml), departmentNoticeUpdaterThreadTaskExecutor)
                    .thenAccept(scrapResults -> compareLatestAndUpdateDB(scrapResults, deptInfo.getDeptName()));
        }
    }

    private List<CommonNoticeFormatDto> updateDepartmentAsync(DeptInfo deptInfo, Function<DeptInfo, List<ScrapingResultDto>> decisionMaker) {
        List<CommonNoticeFormatDto> scrapResults = scrapperTemplate.scrap(deptInfo, decisionMaker);

        // noticeAPIClient 혹은 scraper에서 새로운 공지를 감지할 때, 가장 최신에 올라온 공지를 list에 순차적으로 담는다.
        // 이 때문에 만약 같은 시간대에 감지된 두 공지가 있다면, 보다 최신 공지가 리스트의 앞 인덱스에 위치하게 되고, 이를 그대로 DB에 적용한다면
        // 보다 최신인 공지가 DB에 먼저 삽입되어, kuring API 서버에서 이를 덜 신선한 공지로 판단하게 된다.
        // 이에 commonNoticeFormatDTOList를 reverse하여 공지의 신선도 순서를 유지한다.
        Collections.reverse(scrapResults);

        return scrapResults;
    }

    private void compareLatestAndUpdateDB(List<CommonNoticeFormatDto> scrapResults, String departmentName) {
        DepartmentName departmentNameEnum = DepartmentName.fromKor(departmentName);

        // DB에서 최신 60개의 공지를 가져와서
        List<String> savedArticleIds = departmentNoticeRepository.findArticleIdsByDepartmentWithLimit(departmentNameEnum, TOTAL_NOTICE_COUNT_PER_PAGE * 5);

        // articleId와 DepartmentName이 동일한 공지가 없다면, 새 공지로 인식한다.
        List<DepartmentNotice> newNotices = filteringNewNotice(scrapResults, savedArticleIds, departmentNameEnum);

        departmentNoticeRepository.saveAllAndFlush(newNotices);

        long endTime = System.currentTimeMillis();
        log.info("[학과] 업데이트 시작으로부터 {}millis 만큼 지남", endTime - startTime);
    }

    private List<DepartmentNotice> filteringNewNotice(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, DepartmentName departmentNameEnum) {
        List<DepartmentNotice> newNotices = new LinkedList<>();
        for (CommonNoticeFormatDto notice : scrapResults) {
            try {
                if (!savedArticleIds.contains(notice.getArticleId())) {
                    Category category = categoryMap.get("department");
                    DepartmentNotice newDepartmentNotice = convert(notice, departmentNameEnum, category);
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

    private static DepartmentNotice convert(CommonNoticeFormatDto dto, DepartmentName departmentNameEnum, Category category) {
        return DepartmentNotice.builder()
                .articleId(dto.getArticleId())
                .postedDate(dto.getPostedDate())
                .updatedDate(dto.getUpdatedDate())
                .subject(dto.getSubject())
                .fullUrl(dto.getFullUrl())
                .category(category)
                .departmentName(departmentNameEnum)
                .build();
    }
}

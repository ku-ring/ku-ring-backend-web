package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.message.application.service.FirebaseNotificationService;
import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.KuisHomepageNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.client.notice.LibraryNoticeApiClient;
import com.kustacks.kuring.worker.scrap.noticeinfo.KuisHomepageNoticeInfo;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class KuisHomepageNoticeUpdater {

    private final ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;
    private final FirebaseNotificationService notificationService;
    private final LibraryNoticeApiClient libraryNoticeApiClient;
    private final KuisHomepageNoticeScraperTemplate scrapperTemplate;
    private final NoticeUpdateSupport noticeUpdateSupport;
    private final List<KuisHomepageNoticeInfo> kuisNoticeInfoList;
    private final NoticeCommandPort noticeCommandPort;
    private final NoticeQueryPort noticeQueryPort;

    /*
    학사, 장학, 취창업, 국제, 학생, 산학, 일반, 도서관 공지 갱신
    */
    @Scheduled(cron = "0 0/10 6-23 * * *", zone = "Asia/Seoul") // 학교 공지는 오전 6:00 ~ 오후 11:55분 사이에 10분마다 업데이트 된다.
    public void update() {
        log.info("========== KUIS Hompage 공지 업데이트 시작 ==========");

        updateLibrary(); // library는 Kuis공지가 아니라 별도로 먼저 수행한다

        for (KuisHomepageNoticeInfo kuisNoticeInfo : kuisNoticeInfoList) {
            CompletableFuture
                    .supplyAsync(
                            () -> updateKuisNoticeAsync(kuisNoticeInfo, KuisHomepageNoticeInfo::scrapLatestPageHtml),
                            noticeUpdaterThreadTaskExecutor
                    ).thenApply(
                            scrapResults -> compareLatestAndUpdateDB(scrapResults, kuisNoticeInfo.getCategoryName())
                    ).thenAccept(notificationService::sendNotificationList);
        }
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul") // 전체 업데이트는 매일 오전 1시에 한다.
    public void updateAll() {
        log.info("******** KUIS Hompage 전체 공지 업데이트 시작 ********");

        for (KuisHomepageNoticeInfo kuisNoticeInfo : kuisNoticeInfoList) {
            CompletableFuture
                    .supplyAsync(
                            () -> updateKuisNoticeAsync(kuisNoticeInfo, KuisHomepageNoticeInfo::scrapAllPageHtml),
                            noticeUpdaterThreadTaskExecutor
                    ).thenAccept(
                            scrapResults -> compareAllAndUpdateDB(scrapResults, kuisNoticeInfo.getCategoryName())
                    );
        }
    }

    private void updateLibrary() {
        List<CommonNoticeFormatDto> scrapResults = updateLibraryNotice(CategoryName.LIBRARY);
        List<Notice> notices = compareLibraryLatestAndUpdateDB(scrapResults, CategoryName.LIBRARY);
        notificationService.sendNotificationList(notices);
    }

    private List<CommonNoticeFormatDto> updateLibraryNotice(CategoryName categoryName) {
        return libraryNoticeApiClient.request(categoryName);
    }

    private List<Notice> compareLibraryLatestAndUpdateDB(List<CommonNoticeFormatDto> scrapResults, CategoryName categoryName) {
        // DB에서 모든 일반 공지 id를 가져와서
        List<String> savedArticleIds = noticeQueryPort.findNormalArticleIdsByCategory(categoryName);

        // db와 싱크를 맞춘다
        return synchronizationWithLibraryDb(scrapResults, savedArticleIds, categoryName);
    }

    private List<Notice> synchronizationWithLibraryDb(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, CategoryName categoryName) {
        List<Notice> newNotices = noticeUpdateSupport.filteringSoonSaveNotices(scrapResults, savedArticleIds, categoryName);

        List<String> scrapNoticeIds = noticeUpdateSupport.extractNoticeIds(scrapResults);

        List<String> deletedNoticesArticleIds = noticeUpdateSupport.filteringSoonDeleteNoticeIds(savedArticleIds, scrapNoticeIds);

        noticeCommandPort.saveAllCategoryNotices(newNotices);

        if (!deletedNoticesArticleIds.isEmpty()) {
            noticeCommandPort.deleteAllByIdsAndCategory(categoryName, deletedNoticesArticleIds);
        }

        return newNotices;
    }

    // 여기부터는 kUIS 공지
    private List<ComplexNoticeFormatDto> updateKuisNoticeAsync(KuisHomepageNoticeInfo deptInfo, Function<KuisHomepageNoticeInfo, List<ScrapingResultDto>> decisionMaker) {
        List<ComplexNoticeFormatDto> noticeDtos = scrapperTemplate.scrap(deptInfo, decisionMaker);
        Collections.reverse(noticeDtos);
        return noticeDtos;
    }

    private List<Notice> compareLatestAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, CategoryName categoryName) {
        if (scrapResults.isEmpty()) {
            return Collections.emptyList();
        }

        List<Notice> newNoticeList = new ArrayList<>();
        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 모든 중요 공지를 가져와서
            List<String> savedImportantArticleIds = noticeQueryPort.findImportantArticleIdsByCategoryName(categoryName);

            // db와 싱크를 맞춘다
            List<Notice> newImportantNotices = saveNewNotices(scrapResult.getImportantNoticeList(), savedImportantArticleIds, categoryName, true);
            newNoticeList.addAll(newImportantNotices);

            // DB에서 모든 일반 공지 id를 가져와서
            List<String> savedNormalArticleIds = noticeQueryPort.findNormalArticleIdsByCategoryName(categoryName);

            // db와 싱크를 맞춘다
            List<Notice> newNormalNotices = saveNewNotices(scrapResult.getNormalNoticeList(), savedNormalArticleIds, categoryName, false);
            newNoticeList.addAll(newNormalNotices);
        }

        return newNoticeList;
    }

    private void compareAllAndUpdateDB(List<ComplexNoticeFormatDto> scrapResults, CategoryName categoryName) {
        if (scrapResults.isEmpty()) {
            return;
        }

        for (ComplexNoticeFormatDto scrapResult : scrapResults) {
            // DB에서 모든 중요 공지를 가져와서
            List<String> savedImportantArticleIds = noticeQueryPort.findImportantArticleIdsByCategoryName(categoryName);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getImportantNoticeList(), savedImportantArticleIds, categoryName, true);


            // DB에서 모든 일반 공지 id를 가져와서
            List<String> savedNormalArticleIds = noticeQueryPort.findNormalArticleIdsByCategoryName(categoryName);

            // db와 싱크를 맞춘다
            synchronizationWithDb(scrapResult.getNormalNoticeList(), savedNormalArticleIds, categoryName, false);
        }
    }

    private List<Notice> saveNewNotices(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, CategoryName categoryName, boolean important) {
        List<Notice> newNotices = noticeUpdateSupport.filteringSoonSaveNotices(scrapResults, savedArticleIds, categoryName, important);
        noticeCommandPort.saveAllCategoryNotices(newNotices);
        return newNotices;
    }

    private void synchronizationWithDb(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, CategoryName categoryName, boolean important) {
        List<Notice> newNotices = noticeUpdateSupport.filteringSoonSaveNotices(scrapResults, savedArticleIds, categoryName, important);

        List<String> latestNoticeIds = noticeUpdateSupport.extractNoticeIds(scrapResults);

        List<String> deletedNoticesArticleIds = noticeUpdateSupport.filteringSoonDeleteNoticeIds(savedArticleIds, latestNoticeIds);

        noticeCommandPort.saveAllCategoryNotices(newNotices);

        if (!deletedNoticesArticleIds.isEmpty()) {
            noticeCommandPort.deleteAllByIdsAndCategory(categoryName, deletedNoticesArticleIds);
        }
    }
}

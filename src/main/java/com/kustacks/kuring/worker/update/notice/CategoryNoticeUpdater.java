package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.message.application.service.FirebaseService;
import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.worker.scrap.KuisNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.client.notice.LibraryNoticeApiClient;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisNoticeInfo;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryNoticeUpdater {

    private final List<KuisNoticeInfo> kuisNoticeInfoList;
    private final KuisNoticeScraperTemplate scrapperTemplate;
    private final NoticeQueryPort noticeQueryPort;
    private final NoticeCommandPort noticeCommandPort;
    private final FirebaseService firebaseService;
    private final LibraryNoticeApiClient libraryNoticeApiClient;
    private final ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;
    private final NoticeUpdateSupport noticeUpdateSupport;

    private static long startTime = 0L;

    /*
    학사, 장학, 취창업, 국제, 학생, 산학, 일반, 도서관 공지 갱신
    */
    @Scheduled(cron = "0 0/10 6-23 * * *", zone = "Asia/Seoul") // 학교 공지는 오전 6:00 ~ 오후 11:55분 사이에 20분마다 업데이트 된다.
    public void update() {
        log.info("========== 공지 업데이트 시작 ==========");
        startTime = System.currentTimeMillis();

        updateLibrary(); // library는 Kuis공지가 아니라 별도로 먼저 수행한다

        for (KuisNoticeInfo kuisNoticeInfo : kuisNoticeInfoList) {
            CompletableFuture
                    .supplyAsync(() -> updateKuisNoticeAsync(kuisNoticeInfo, KuisNoticeInfo::scrapLatestPageHtml), noticeUpdaterThreadTaskExecutor)
                    .thenApply(scrapResults -> compareLatestAndUpdateDB(scrapResults, kuisNoticeInfo.getCategoryName()))
                    .thenAccept(firebaseService::sendNotificationList);
        }
    }

    private void updateLibrary() {
        List<CommonNoticeFormatDto> scrapResults = updateLibraryNotice(CategoryName.LIBRARY);
        List<Notice> notices = compareLatestAndUpdateDB(scrapResults, CategoryName.LIBRARY);
        firebaseService.sendNotificationList(notices);
    }

    private List<CommonNoticeFormatDto> updateLibraryNotice(CategoryName categoryName) {
        return libraryNoticeApiClient.request(categoryName);
    }

    private List<CommonNoticeFormatDto> updateKuisNoticeAsync(KuisNoticeInfo deptInfo, Function<KuisNoticeInfo, List<CommonNoticeFormatDto>> decisionMaker) {
        return scrapperTemplate.scrap(deptInfo, decisionMaker);
    }

    private List<Notice> compareLatestAndUpdateDB(List<CommonNoticeFormatDto> scrapResults, CategoryName categoryName) {
        // DB에서 모든 일반 공지 id를 가져와서
        List<String> savedArticleIds = noticeQueryPort.findNormalArticleIdsByCategory(categoryName);

        // db와 싱크를 맞춘다
        List<Notice> newNotices = synchronizationWithDb(scrapResults, savedArticleIds, categoryName);

        long endTime = System.currentTimeMillis();
        log.info("[{}] 업데이트 시작으로부터 {}millis 만큼 지남", categoryName.getKorName(), endTime - startTime);

        return newNotices;
    }

    private List<Notice> synchronizationWithDb(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, CategoryName categoryName) {
        List<Notice> newNotices = noticeUpdateSupport.filteringSoonSaveNotices(scrapResults, savedArticleIds, categoryName);

        List<String> scrapNoticeIds = noticeUpdateSupport.extractNoticeIds(scrapResults);

        List<String> deletedNoticesArticleIds = noticeUpdateSupport.filteringSoonDeleteNoticeIds(savedArticleIds, scrapNoticeIds);

        noticeCommandPort.saveAllCategoryNotices(newNotices);

        if (!deletedNoticesArticleIds.isEmpty()) {
            noticeCommandPort.deleteAllByIdsAndCategory(categoryName, deletedNoticesArticleIds);
        }

        return newNotices;
    }
}

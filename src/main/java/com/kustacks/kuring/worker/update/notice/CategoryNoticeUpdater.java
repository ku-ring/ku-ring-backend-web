package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.worker.scrap.KuisNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.client.notice.LibraryNoticeApiClient;
import com.kustacks.kuring.worker.update.notice.dto.request.KuisNoticeInfo;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryNoticeUpdater {

    private final List<KuisNoticeInfo> kuisNoticeInfoList;
    private final KuisNoticeScraperTemplate scrapperTemplate;
    private final NoticeRepository noticeRepository;
    private final FirebaseService firebaseService;
    private final LibraryNoticeApiClient libraryNoticeApiClient;
    private final ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;

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
                    .thenAccept(firebaseService::sendNotificationByFcm);
        }
    }

    private void updateLibrary() {
        List<CommonNoticeFormatDto> scrapResults = updateLibraryNotice(CategoryName.LIBRARY);
        List<Notice> notices = compareLatestAndUpdateDB(scrapResults, CategoryName.LIBRARY);
        firebaseService.sendNotificationByFcm(notices);
    }

    private List<CommonNoticeFormatDto> updateLibraryNotice(CategoryName categoryName) {
        List<CommonNoticeFormatDto> scrapResults = libraryNoticeApiClient.request(categoryName);
        Collections.reverse(scrapResults);
        return scrapResults;
    }

    private List<CommonNoticeFormatDto> updateKuisNoticeAsync(KuisNoticeInfo deptInfo, Function<KuisNoticeInfo, List<CommonNoticeFormatDto>> decisionMaker) {
        List<CommonNoticeFormatDto> scrapResults = scrapperTemplate.scrap(deptInfo, decisionMaker);
        Collections.reverse(scrapResults);
        return scrapResults;
    }

    private List<Notice> compareLatestAndUpdateDB(List<CommonNoticeFormatDto> scrapResults, CategoryName categoryName) {
        // DB에서 모든 일반 공지 id를 가져와서
        List<String> savedArticleIds = noticeRepository.findNormalArticleIdsByCategory(categoryName);

        // db와 싱크를 맞춘다
        List<Notice> newNotices = synchronizationWithDb(scrapResults, savedArticleIds, categoryName);

        long endTime = System.currentTimeMillis();
        log.info("[{}] 업데이트 시작으로부터 {}millis 만큼 지남", categoryName.getKorName(), endTime - startTime);

        return newNotices;
    }

    private List<Notice> synchronizationWithDb(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, CategoryName categoryName) {
        List<Notice> newNotices = filteringSoonSaveNotices(scrapResults, savedArticleIds, categoryName);

        List<String> scrapNoticeIds = extractIdList(scrapResults);

        List<String> deletedNoticesArticleIds = filteringSoonDeleteIds(savedArticleIds, scrapNoticeIds);

        noticeRepository.saveAllAndFlush(newNotices);

        if (!deletedNoticesArticleIds.isEmpty()) {
            noticeRepository.deleteAllByIdsAndCategory(categoryName, deletedNoticesArticleIds);
        }

        return newNotices;
    }

    private List<String> extractIdList(List<CommonNoticeFormatDto> scrapResults) {
        return scrapResults.stream()
                .map(CommonNoticeFormatDto::getArticleId)
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> filteringSoonDeleteIds(List<String> savedArticleIds, List<String> latestNoticeIds) {
        return savedArticleIds.stream()
                .filter(savedArticleId -> Collections.binarySearch(latestNoticeIds, savedArticleId) < 0)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    private List<Notice> filteringSoonSaveNotices(List<CommonNoticeFormatDto> scrapResults, List<String> savedArticleIds, CategoryName categoryName) {
        List<Notice> newNotices = new LinkedList<>(); // 뒤에 추가만 계속 하기 때문에 arrayList가 아닌 Linked List 사용 O(1)
        for (CommonNoticeFormatDto notice : scrapResults) {
            try {
                if (Collections.binarySearch(savedArticleIds, notice.getArticleId()) < 0) { // 정렬되어있다, 이진탐색으로 O(logN)안에 수행
                    Notice newNotice = convert(notice, categoryName);
                    newNotices.add(newNotice);
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

    private Notice convert(CommonNoticeFormatDto dto, CategoryName CategoryName) {
        return new Notice(dto.getArticleId(),
                dto.getPostedDate(),
                dto.getUpdatedDate(),
                dto.getSubject(),
                CategoryName,
                false,
                dto.getFullUrl());
    }
}

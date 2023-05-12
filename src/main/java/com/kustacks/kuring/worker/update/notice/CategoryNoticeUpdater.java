package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.firebase.exception.FirebaseMessageSendException;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.update.Updater;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryNoticeUpdater implements Updater {

    private final Map<CategoryName, NoticeApiClient<CommonNoticeFormatDto, CategoryName>> noticeApiClientMap;
    private final FirebaseService firebaseService;
    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;

    private Map<String, Category> categoryMap;

    public CategoryNoticeUpdater(FirebaseService firebaseService,
                                 Map<CategoryName, NoticeApiClient<CommonNoticeFormatDto, CategoryName>> noticeApiClientMap,
                                 NoticeRepository noticeRepository,
                                 CategoryRepository categoryRepository) {
        this.firebaseService = firebaseService;
        this.noticeApiClientMap = noticeApiClientMap;
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void update() {
        log.info("========== 공지 업데이트 시작 ==========");

        Map<CategoryName, List<CommonNoticeFormatDto>> apiNoticesMap = scrapNewNotices();

        List<Notice> willBeNotificationNotices = compareAndUpdateDB(apiNoticesMap);

        List<NoticeMessageDto> notificationDtoList = createNotification(willBeNotificationNotices);

        sendNotificationByFcm(notificationDtoList);

        log.info("========== 공지 업데이트 종료 ==========");
    }

    /*
    학사, 장학, 취창업, 국제, 학생, 산학, 일반, 도서관 공지 갱신
    */
    private Map<CategoryName, List<CommonNoticeFormatDto>> scrapNewNotices() {
        Map<CategoryName, List<CommonNoticeFormatDto>> apiNoticesMap = new HashMap<>();

        for (CategoryName categoryName : CategoryName.values()) {
            if (categoryName == CategoryName.DEPARTMENT) {
                continue;
            }

            try {
                List<CommonNoticeFormatDto> commonNoticeFormatDTO = noticeApiClientMap.get(categoryName).request(categoryName);
                apiNoticesMap.put(categoryName, commonNoticeFormatDTO);
            } catch (InternalLogicException e) {
                log.info("{}", e.getErrorCode().getMessage());
                if (ErrorCode.KU_LOGIN_BAD_RESPONSE.equals(e.getErrorCode())) {
                    Sentry.captureException(e);
                }
            }
        }

        return apiNoticesMap;
    }

    private List<Notice> compareAndUpdateDB(Map<CategoryName, List<CommonNoticeFormatDto>> apiNoticesMap) {
        // DB에 있는 공지 데이터 카테고리별로 꺼내와서
        // kuisNoticeResponseBody에 있는 데이터가 DB에는 없는 경우 -> DB에 공지 추가
        // DB에 있는 데이터가 kuisNoticeResponseBody에는 없는 경우 -> DB에 공지 삭제

        if (categoryMap == null) {
            categoryMap = categoryRepository.findAllMap();
        }

        List<Notice> willBeNotiNotices = new LinkedList<>();
        for (CategoryName categoryName : apiNoticesMap.keySet()) {
            String categoryFullName = categoryName.getName();
            Category noticeCategory = categoryMap.get(categoryFullName);

            // categoryName에 대응하는, ku api로 받아온 공지 데이터
            List<CommonNoticeFormatDto> commonNoticeFormatDtoList = apiNoticesMap.get(categoryName);

            // categoryName에 대응하는, DB에 존재하는 공지 데이터
            Map<String, Notice> dbNoticeMap = noticeRepository.findNoticeMapByCategory(noticeCategory);

            // commonNoticeFormatDTOList를 순회하면서
            // 현재 공지가 dbNoticeList에 있으면 dbNoticeList에서 해당 공지 없애고(실제 DB에는 아무런 작업 안함)
            // dbNoticeList에 없다면 공지 추가 (실제 DB에 추가)
            // 작업이 끝난 후 dbNoticeList에 공지가 남아있다면, 해당 공지들은 DB에서 삭제 (실제 DB에 삭제)
            List<Notice> newNotices = new LinkedList<>(); // DB에 추가할 공지 임시 저장
            Iterator<CommonNoticeFormatDto> noticeIterator = commonNoticeFormatDtoList.iterator();
            while (noticeIterator.hasNext()) {
                CommonNoticeFormatDto apiNotice = noticeIterator.next();
                Notice notice = dbNoticeMap.get(apiNotice.getArticleId());
                if (notice == null) {
                    newNotices.add(new Notice(apiNotice.getArticleId(),
                            apiNotice.getPostedDate(),
                            apiNotice.getUpdatedDate(),
                            apiNotice.getSubject(),
                            noticeCategory,
                            false,
                            apiNotice.getFullUrl()));
                } else {
                    noticeIterator.remove();
                    dbNoticeMap.remove(apiNotice.getArticleId());
                }
            }

            // 업데이트로 인해 없어져야될 공지 삭제
            Collection<Notice> removedNotices = dbNoticeMap.values();
            noticeRepository.deleteAll(removedNotices);

            // 업데이트로 인해 새로 생성된 공지 삽입
            noticeRepository.saveAllAndFlush(newNotices);

            willBeNotiNotices.addAll(newNotices);
        }

        return willBeNotiNotices;
    }

    private List<NoticeMessageDto> createNotification(List<Notice> willBeNotiNotices) {
        return willBeNotiNotices.stream()
                .map(NoticeMessageDto::from)
                .collect(Collectors.toList());
    }

    private void sendNotificationByFcm(List<NoticeMessageDto> willBeNotiNoticeDtoList) {
        try {
            firebaseService.sendNoticeMessageList(willBeNotiNoticeDtoList);
            log.info("FCM에 정상적으로 메세지를 전송했습니다.");
            log.info("전송된 공지 목록은 다음과 같습니다.");
            for (NoticeMessageDto messageDTO : willBeNotiNoticeDtoList) {
                log.info("아이디 = {}, 날짜 = {}, 카테고리 = {}, 제목 = {}", messageDTO.getArticleId(), messageDTO.getPostedDate(), messageDTO.getCategory(), messageDTO.getSubject());
            }
        } catch (FirebaseMessageSendException e) {
            log.error("새로운 공지의 FCM 전송에 실패했습니다.");
            throw new InternalLogicException(ErrorCode.FB_FAIL_SEND, e);
        } catch (Exception e) {
            log.error("새로운 공지를 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.");
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e);
        }
    }
}

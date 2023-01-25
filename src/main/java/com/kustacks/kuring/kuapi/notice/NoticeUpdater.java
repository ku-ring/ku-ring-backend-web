package com.kustacks.kuring.kuapi.notice;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.Updater;
import com.kustacks.kuring.kuapi.api.notice.NoticeAPIClient;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.utils.converter.DTOConverter;
import com.kustacks.kuring.common.utils.converter.DateConverter;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NoticeUpdater implements Updater {

    private final Map<CategoryName, NoticeAPIClient> noticeAPIClientMap;
    private final DTOConverter dtoConverter;
    private final DateConverter dateConverter;
    private final FirebaseService firebaseService;
    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;

    private Map<String, Category> categoryMap;

    public NoticeUpdater(FirebaseService firebaseService,

                         DTOConverter noticeEntityToNoticeMessageDTOConverter,
                         DateConverter ymdhmsToYmdConverter,
                         Map<CategoryName, NoticeAPIClient> noticeAPIClientMap,

                         NoticeRepository noticeRepository,
                         CategoryRepository categoryRepository) {

        this.dtoConverter = noticeEntityToNoticeMessageDTOConverter;
        this.dateConverter = ymdhmsToYmdConverter;
        this.noticeAPIClientMap = noticeAPIClientMap;

        this.firebaseService = firebaseService;

        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void update() {

        log.info("========== 공지 업데이트 시작 ==========");

        /*
            학사, 장학, 취창업, 국제, 학생, 산학, 일반, 도서관 공지 갱신
         */
        Map<CategoryName, List<CommonNoticeFormatDTO>> apiNoticesMap = new HashMap<>(); // 수신한 공지 데이터를 저장할 변수
        for (CategoryName categoryName : CategoryName.values()) {
            List<CommonNoticeFormatDTO> commonNoticeFormatDTO;

            try {
                commonNoticeFormatDTO = noticeAPIClientMap.get(categoryName).getNotices(categoryName);
                apiNoticesMap.put(categoryName, commonNoticeFormatDTO);
            } catch (InternalLogicException e) {
                log.info("{}", e.getErrorCode().getMessage());
                if(ErrorCode.KU_LOGIN_BAD_RESPONSE.equals(e.getErrorCode())) {
                    Sentry.captureException(e);
                }
            }
        }

        // DB에 있는 공지 데이터 카테고리별로 꺼내와서
        // kuisNoticeResponseBody에 있는 데이터가 DB에는 없는 경우 -> DB에 공지 추가
        // DB에 있는 데이터가 kuisNoticeResponseBody에는 없는 경우 -> DB에 공지 삭제
        List<Notice> willBeNotiNotices = compareAndUpdateDB(apiNoticesMap);
        List<NoticeMessageDto> willBeNotiNoticeDTOList = new ArrayList<>(willBeNotiNotices.size());
        for (Notice notice : willBeNotiNotices) {
            if(CategoryName.LIBRARY.getName().equals(notice.getCategory().getName())) {
                // TODO: notice entity 내용을 변경해서 사용하는게 좋은 방법인지는 생각을 해봐야함
                // 혹시나 compareAndUpdateDB에서 영속성 컨텍스트에 남아있는 notice entity의 내용이 변경되어서 저장될까봐
                // compareAndUpdateDB에서 saveAndFlush 메서드를 사용함.
                notice.setPostedDate(dateConverter.convert(notice.getPostedDate()));
            }
            willBeNotiNoticeDTOList.add((NoticeMessageDto) dtoConverter.convert(notice));
        }

        // FCM으로 새롭게 수신한 공지 데이터 전송
        try {
            firebaseService.sendMessage(willBeNotiNoticeDTOList);
            log.info("FCM에 정상적으로 메세지를 전송했습니다.");
            log.info("전송된 공지 목록은 다음과 같습니다.");
            for (NoticeMessageDto messageDTO : willBeNotiNoticeDTOList) {
                log.info("아이디 = {}, 날짜 = {}, 카테고리 = {}, 제목 = {}", messageDTO.getArticleId(), messageDTO.getPostedDate(), messageDTO.getCategory(), messageDTO.getSubject());
            }
        } catch(FirebaseMessagingException e) {
            log.error("새로운 공지의 FCM 전송에 실패했습니다.");
            throw new InternalLogicException(ErrorCode.FB_FAIL_SEND, e);
        } catch(Exception e) {
            log.error("새로운 공지를 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.");
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e);
        }

        log.info("========== 공지 업데이트 종료 ==========");
    }

    private List<Notice> compareAndUpdateDB(Map<CategoryName, List<CommonNoticeFormatDTO>> apiNoticesMap) {

        if(categoryMap == null) {
            categoryMap = categoryRepository.findAllMap();
        }

        List<Notice> willBeNotiNotices = new LinkedList<>();
        for (CategoryName categoryName : apiNoticesMap.keySet()) {
            String categoryFullName = categoryName.getName();
            Category noticeCategory = categoryMap.get(categoryFullName);

            // categoryName에 대응하는, ku api로 받아온 공지 데이터
            List<CommonNoticeFormatDTO> commonNoticeFormatDTOList = apiNoticesMap.get(categoryName);

            // categoryName에 대응하는, DB에 존재하는 공지 데이터
            Map<String, Notice> dbNoticeMap = noticeRepository.findByCategoryMap(noticeCategory);

            // commonNoticeFormatDTOList를 순회하면서
            // 현재 공지가 dbNoticeList에 있으면 dbNoticeList에서 해당 공지 없애고(실제 DB에는 아무런 작업 안함)
            // dbNoticeList에 없다면 공지 추가 (실제 DB에 추가)
            // 작업이 끝난 후 dbNoticeList에 공지가 남아있다면, 해당 공지들은 DB에서 삭제 (실제 DB에 삭제)
            List<Notice> newNotices = new LinkedList<>(); // DB에 추가할 공지 임시 저장
            Iterator<CommonNoticeFormatDTO> noticeIterator = commonNoticeFormatDTOList.iterator();
            while(noticeIterator.hasNext()) {
                CommonNoticeFormatDTO apiNotice = noticeIterator.next();
                Notice notice = dbNoticeMap.get(apiNotice.getArticleId());
                if(notice == null) {
                    newNotices.add(Notice.builder()
                            .articleId(apiNotice.getArticleId())
                            .postedDate(apiNotice.getPostedDate())
                            .updatedDate(apiNotice.getUpdatedDate())
                            .subject(apiNotice.getSubject())
                            .category(noticeCategory)
                            .build());
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
}

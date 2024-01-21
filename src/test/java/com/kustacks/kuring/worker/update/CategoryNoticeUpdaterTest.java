package com.kustacks.kuring.worker.update;

import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.worker.scrap.KuisNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.client.notice.LibraryNoticeApiClient;
import com.kustacks.kuring.worker.update.notice.CategoryNoticeUpdater;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;


@SpringBootTest
@TestPropertySource(properties = "spring.config.location=" +
        "classpath:/application.yml" +
        ",classpath:/application-test.yml" +
        ",classpath:/test-constants.properties")
class CategoryNoticeUpdaterTest {

    @MockBean
    KuisNoticeScraperTemplate scrapperTemplate;

    @MockBean
    FirebaseService firebaseService;

    @MockBean
    LibraryNoticeApiClient libraryNoticeApiClient;

    @Autowired
    CategoryNoticeUpdater categoryNoticeUpdater;

    @Autowired
    ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;

    @Autowired
    NoticeRepository noticeRepository;

    @DisplayName("공지 업데이트 테스트")
    @Test
    void notice_scrap_async_test() throws InterruptedException {
        // given
        doReturn(createNoticesFixture()).when(scrapperTemplate).scrap(any(), any());
        doReturn(createLibraryFixture()).when(libraryNoticeApiClient).request(any());
        doNothing().when(firebaseService).sendNotificationList(anyList());

        // when
        categoryNoticeUpdater.update();
        noticeUpdaterThreadTaskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);

        // then
        List<Notice> notices = noticeRepository.findAll();
        assertAll(
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "library").size().isEqualTo(7),
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "bachelor").size().isEqualTo(9),
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "scholarship").size().isEqualTo(9),
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "employment").size().isEqualTo(9),
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "national").size().isEqualTo(9),
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "student").size().isEqualTo(9),
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "industry_university").size().isEqualTo(9),
                () -> Assertions.assertThat(notices).filteredOn("categoryName", "normal").size().isEqualTo(9)
        );
    }

    private static List<CommonNoticeFormatDto> createLibraryFixture() {
        return List.of(
                CommonNoticeFormatDto.builder().articleId("1").updatedDate("2021-01-01").subject("library1")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("2").updatedDate("2021-01-01").subject("library2")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71922").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("3").updatedDate("2021-01-01").subject("library3")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71923").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("4").updatedDate("2021-01-01").subject("library4")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71924").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("5").updatedDate("2021-01-01").subject("library5")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71925").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("6").updatedDate("2021-01-01").subject("library6")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71926").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("7").updatedDate("2021-01-01").subject("library7")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71927").important(false).build()
        );
    }

    private static List<CommonNoticeFormatDto> createNoticesFixture() {
        return List.of(
                CommonNoticeFormatDto.builder().articleId("1").updatedDate("2021-01-01").subject("library1")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("2").updatedDate("2021-01-01").subject("library2")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71922").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("3").updatedDate("2021-01-01").subject("library3")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71923").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("4").updatedDate("2021-01-01").subject("library4")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71924").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("5").updatedDate("2021-01-01").subject("library5")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71925").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("6").updatedDate("2021-01-01").subject("library6")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71926").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("7").updatedDate("2021-01-01").subject("library7")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71927").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("8").updatedDate("2021-01-01").subject("library8")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71928").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("9").updatedDate("2021-01-01").subject("library9")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71929").important(false).build()
        );
    }
}
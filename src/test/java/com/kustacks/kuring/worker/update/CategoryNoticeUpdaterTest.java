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
public class CategoryNoticeUpdaterTest {

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
    public void notice_scrap_async_test() throws InterruptedException {
        // given
        doReturn(createNoticesFixture()).when(scrapperTemplate).scrap(any(), any());
        doReturn(createLibraryFixture()).when(libraryNoticeApiClient).request(any());
        doNothing().when(firebaseService).sendNotificationList(anyList());

        // when
        categoryNoticeUpdater.update();
        noticeUpdaterThreadTaskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
        List<Notice> notices = noticeRepository.findAll();

        // then
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
                new CommonNoticeFormatDto("1", "2021-01-01", "library1", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921", false),
                new CommonNoticeFormatDto("2", "2021-01-01", "library2", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71922", false),
                new CommonNoticeFormatDto("3", "2021-01-01", "library3", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71923", false),
                new CommonNoticeFormatDto("4", "2021-01-01", "library4", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71924", false),
                new CommonNoticeFormatDto("5", "2021-01-01", "library5", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71925", false),
                new CommonNoticeFormatDto("6", "2021-01-01", "library6", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71926", false),
                new CommonNoticeFormatDto("7", "2021-01-01", "library7", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71927", false)
        );
    }

    private static List<CommonNoticeFormatDto> createNoticesFixture() {
        return List.of(
                new CommonNoticeFormatDto("1", "2021-01-01", "subject1", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921", false),
                new CommonNoticeFormatDto("2", "2021-01-01", "subject2", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71922", false),
                new CommonNoticeFormatDto("3", "2021-01-01", "subject3", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71923", false),
                new CommonNoticeFormatDto("4", "2021-01-01", "subject4", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71924", false),
                new CommonNoticeFormatDto("5", "2021-01-01", "subject5", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71925", false),
                new CommonNoticeFormatDto("6", "2021-01-01", "subject6", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71926", false),
                new CommonNoticeFormatDto("7", "2021-01-01", "subject7", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71927", false),
                new CommonNoticeFormatDto("8", "2021-01-01", "subject8", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71928", false),
                new CommonNoticeFormatDto("9", "2021-01-01", "subject9", "2021-01-01", "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71929", false)
        );
    }
}

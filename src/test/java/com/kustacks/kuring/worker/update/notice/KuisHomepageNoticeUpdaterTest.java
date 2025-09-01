package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.scrap.KuisHomepageNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.client.notice.LibraryNoticeApiClient;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@Disabled
class KuisHomepageNoticeUpdaterTest extends IntegrationTestSupport {

    @MockBean
    KuisHomepageNoticeScraperTemplate scrapperTemplate;

    @MockBean
    LibraryNoticeApiClient libraryNoticeApiClient;

    @Autowired
    KuisHomepageNoticeUpdater kuisHomepageNoticeUpdater;

    @Autowired
    ThreadPoolTaskExecutor noticeUpdaterThreadTaskExecutor;

    @Autowired
    NoticeQueryPort noticeQueryPort;

    @Autowired
    NoticeCommandPort noticeCommandPort;

    @DisplayName("Kuis 공지를 학교 홈페이지로부터 업데이트 하는 테스트")
    @Test
    void notice_scrap_async_test() throws InterruptedException {
        // given
        doReturn(createNoticesFixture()).when(scrapperTemplate).scrap(any(), any());
        doReturn(createLibraryFixture()).when(libraryNoticeApiClient).request(any());

        // when
        kuisHomepageNoticeUpdater.update();
        noticeUpdaterThreadTaskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);

        // then
        Long count = noticeQueryPort.count();
        assertThat(count).isEqualTo(150); // 여기 테스트에서 133개 + DatabaseConfigurator initNotice() 과정에서 17개
    }

    @DisplayName("Kuis 공지중 중요도가 중요에서 일반으로 바뀐 공지를 업데이트 하는 테스트")
    @Test
    void notice_update_important_test() throws InterruptedException {
        // given
        Notice noticeOne = new Notice("1", "2021-01-01", "2021-01-01", "library1",
                CategoryName.BACHELOR,
                true, "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921");
        Notice noticeTwo = new Notice("2", "2021-01-01", "2021-01-01", "library2",
                CategoryName.BACHELOR,
                true, "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921");
        Notice noticeThree = new Notice("3", "2021-01-01", "2021-01-01", "library3",
                CategoryName.BACHELOR,
                true, "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921");
        Notice noticeFour = new Notice("4", "2021-01-01", "2021-01-01", "library4",
                CategoryName.BACHELOR,
                false, "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921");
        Notice noticeFive = new Notice("5", "2021-01-01", "2021-01-01", "library5",
                CategoryName.BACHELOR,
                false, "https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921");

        noticeCommandPort.saveAllCategoryNotices(List.of(noticeOne, noticeTwo, noticeThree, noticeFour, noticeFive));

        List<CommonNoticeFormatDto> importantNotices = Arrays.asList(
                CommonNoticeFormatDto.builder().articleId("2").updatedDate("2021-01-01").subject("library2")
                        .postedDate("2021-01-01")
                        .fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921")
                        .important(true).build(),

                CommonNoticeFormatDto.builder().articleId("6").updatedDate("2021-01-01").subject("library6")
                        .postedDate("2021-01-01")
                        .fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921")
                        .important(true).build()
        );

        List<CommonNoticeFormatDto> normalNotices = Arrays.asList(
                CommonNoticeFormatDto.builder().articleId("3").updatedDate("2021-01-01").subject("library3")
                        .postedDate("2021-01-01")
                        .fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921")
                        .important(false).build(),

                CommonNoticeFormatDto.builder().articleId("4").updatedDate("2021-01-01").subject("library4")
                        .postedDate("2021-01-01")
                        .fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921")
                        .important(false).build()
        );

        ComplexNoticeFormatDto complexNoticeFormatDto = new ComplexNoticeFormatDto(importantNotices, normalNotices);


        // when
        kuisHomepageNoticeUpdater.compareAllAndUpdateDB(List.of(complexNoticeFormatDto), CategoryName.BACHELOR);

        // then
        List<NoticeDto> findNotices = noticeQueryPort
                .findNoticesByCategoryWithOffset(CategoryName.BACHELOR, PageRequest.of(0, 10));
        assertAll(
                () -> assertThat(findNotices).hasSize(4),
                () -> assertThat(findNotices)
                        .extracting("articleId", "subject", "important")
                        .containsExactlyInAnyOrder(
                                tuple("2", "library2", true),
                                tuple("6", "library6", true),
                                tuple("3", "library3", false),
                                tuple("4", "library4", false)
                        )
        );
    }

    private static List<CommonNoticeFormatDto> createLibraryFixture() {
        return Arrays.asList(
                CommonNoticeFormatDto.builder().articleId("l1").updatedDate("2021-01-01").subject("library1")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("l2").updatedDate("2021-01-01").subject("library2")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71922").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("l3").updatedDate("2021-01-01").subject("library3")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71923").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("l4").updatedDate("2021-01-01").subject("library4")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71924").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("l5").updatedDate("2021-01-01").subject("library5")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71925").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("l6").updatedDate("2021-01-01").subject("library6")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71926").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("l7").updatedDate("2021-01-01").subject("library7")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71927").important(false).build()
        );
    }

    private static List<ComplexNoticeFormatDto> createNoticesFixture() {
        return Arrays.asList(
                new ComplexNoticeFormatDto(createNormalNoticesFixture(), createImportantNoticesFixture())
        );
    }

    private static List<CommonNoticeFormatDto> createNormalNoticesFixture() {
        return Arrays.asList(
                CommonNoticeFormatDto.builder().articleId("n1").updatedDate("2021-01-01").subject("library1")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n2").updatedDate("2021-01-01").subject("library2")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71922").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n3").updatedDate("2021-01-01").subject("library3")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71923").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n4").updatedDate("2021-01-01").subject("library4")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71924").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n5").updatedDate("2021-01-01").subject("library5")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71925").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n6").updatedDate("2021-01-01").subject("library6")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71926").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n7").updatedDate("2021-01-01").subject("library7")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71927").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n8").updatedDate("2021-01-01").subject("library8")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71928").important(false).build(),
                CommonNoticeFormatDto.builder().articleId("n9").updatedDate("2021-01-01").subject("library9")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71929").important(false).build()
        );
    }

    private static List<CommonNoticeFormatDto> createImportantNoticesFixture() {
        return Arrays.asList(
                CommonNoticeFormatDto.builder().articleId("i1").updatedDate("2021-01-01").subject("normal1")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i2").updatedDate("2021-01-01").subject("normal2")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71922").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i3").updatedDate("2021-01-01").subject("normal3")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71923").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i4").updatedDate("2021-01-01").subject("normal4")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71924").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i5").updatedDate("2021-01-01").subject("normal5")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71925").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i6").updatedDate("2021-01-01").subject("normal6")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71926").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i7").updatedDate("2021-01-01").subject("normal7")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71927").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i8").updatedDate("2021-01-01").subject("normal8")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71928").important(true).build(),
                CommonNoticeFormatDto.builder().articleId("i9").updatedDate("2021-01-01").subject("normal9")
                        .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71929").important(true).build()
        );
    }
}

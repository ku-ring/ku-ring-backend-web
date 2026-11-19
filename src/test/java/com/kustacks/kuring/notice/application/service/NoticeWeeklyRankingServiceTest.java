package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeViewCountDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.NoticeViewSnapshot;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : NoticeWeeklyRankingService")
@ExtendWith(MockitoExtension.class)
class NoticeWeeklyRankingServiceTest {

    // 2026-10-25(일) 23:00 (Asia/Seoul), 해당 주의 시작일은 2026-10-19(월)
    private static final Clock SUNDAY_NIGHT_CLOCK = Clock.fixed(
            Instant.parse("2026-10-25T14:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );
    private static final LocalDate WEEK_START_DATE = LocalDate.of(2026, 10, 19);

    @Mock
    private NoticeQueryPort noticeQueryPort;

    @Mock
    private NoticeRankingQueryPort noticeRankingQueryPort;

    @Mock
    private NoticeRankingCommandPort noticeRankingCommandPort;

    @Captor
    private ArgumentCaptor<List<NoticeWeeklyRanking>> rankingsCaptor;

    @Captor
    private ArgumentCaptor<List<NoticeViewSnapshot>> snapshotsCaptor;

    private NoticeWeeklyRankingService noticeWeeklyRankingService;

    @BeforeEach
    void setUp() {
        noticeWeeklyRankingService = new NoticeWeeklyRankingService(
                noticeQueryPort,
                noticeRankingQueryPort,
                noticeRankingCommandPort,
                SUNDAY_NIGHT_CLOCK
        );
    }

    @DisplayName("누적 조회수가 아닌 지난 집계 이후 늘어난 조회수 기준으로 상위 5개 공지의 순위를 저장한다")
    @Test
    void rankWeeklyTopNotices() {
        // given
        when(noticeQueryPort.findAllViewCountsExceptDepartment()).thenReturn(List.of(
                viewCount(1L, "1", CategoryName.BACHELOR, 22000, LocalDateTime.of(2024, 7, 25, 9, 0)),
                viewCount(2L, "2", CategoryName.SCHOLARSHIP, 500, LocalDateTime.of(2026, 10, 20, 9, 0)),
                viewCount(3L, "3", CategoryName.STUDENT, 3000, LocalDateTime.of(2026, 10, 1, 9, 0)),
                viewCount(4L, "4", CategoryName.LIBRARY, 900, LocalDateTime.of(2026, 10, 10, 9, 0)),
                viewCount(5L, "5", CategoryName.NORMAL, 50, LocalDateTime.of(2026, 10, 12, 9, 0)),
                viewCount(6L, "6", CategoryName.EMPLOYMENT, 300, LocalDateTime.of(2026, 10, 22, 9, 0)),
                viewCount(7L, "7", CategoryName.NATIONAL, 400, LocalDateTime.of(2026, 10, 5, 9, 0)),
                viewCount(8L, "8", CategoryName.INDUSTRY_UNIVERSITY, 250, LocalDateTime.of(2026, 10, 6, 9, 0)),
                viewCount(9L, "9", CategoryName.BACHELOR, 0, LocalDateTime.of(2026, 10, 2, 9, 0)) // 아직 조회수가 수집되지 않은 공지
        ));
        when(noticeRankingQueryPort.findAllViewSnapshots()).thenReturn(List.of(
                new NoticeViewSnapshot(CategoryName.BACHELOR, "1", 21000),
                new NoticeViewSnapshot(CategoryName.STUDENT, "1", 21900), // 다른 카테고리의 같은 articleId는 영향을 주지 않는다
                new NoticeViewSnapshot(CategoryName.LIBRARY, "4", 100),
                new NoticeViewSnapshot(CategoryName.NORMAL, "5", 80), // 조회수가 줄어든 경우는 0으로 본다
                new NoticeViewSnapshot(CategoryName.NATIONAL, "7", 200),
                new NoticeViewSnapshot(CategoryName.INDUSTRY_UNIVERSITY, "8", 100)
        ));

        // when
        noticeWeeklyRankingService.rankWeeklyTopNotices();

        // then
        verify(noticeRankingCommandPort).saveWeeklyRankings(eq(WEEK_START_DATE), rankingsCaptor.capture());
        verify(noticeRankingCommandPort).replaceViewSnapshots(snapshotsCaptor.capture());

        assertAll(
                // 3번은 스냅샷이 없고 이번 주 이전에 게시되어 제외, 5번은 조회수가 늘지 않아 제외, 8번은 6위
                () -> assertThat(rankingsCaptor.getValue())
                        .extracting("ranking", "articleId", "categoryName", "viewCount", "weekStartDate")
                        .containsExactly(
                                tuple(1, "1", CategoryName.BACHELOR, 1000, WEEK_START_DATE),
                                tuple(2, "4", CategoryName.LIBRARY, 800, WEEK_START_DATE),
                                tuple(3, "2", CategoryName.SCHOLARSHIP, 500, WEEK_START_DATE),
                                tuple(4, "6", CategoryName.EMPLOYMENT, 300, WEEK_START_DATE),
                                tuple(5, "7", CategoryName.NATIONAL, 200, WEEK_START_DATE)
                        ),
                // 조회수가 0인 9번은 기준값으로 남기지 않는다
                () -> assertThat(snapshotsCaptor.getValue())
                        .extracting("categoryName", "articleId", "viewCount")
                        .containsExactlyInAnyOrder(
                                tuple(CategoryName.BACHELOR, "1", 22000),
                                tuple(CategoryName.SCHOLARSHIP, "2", 500),
                                tuple(CategoryName.STUDENT, "3", 3000),
                                tuple(CategoryName.LIBRARY, "4", 900),
                                tuple(CategoryName.NORMAL, "5", 50),
                                tuple(CategoryName.EMPLOYMENT, "6", 300),
                                tuple(CategoryName.NATIONAL, "7", 400),
                                tuple(CategoryName.INDUSTRY_UNIVERSITY, "8", 250)
                        )
        );
    }

    @DisplayName("이번 주 순위가 이미 집계되어 있으면 다시 집계하지 않고 순위와 기준값을 그대로 둔다")
    @Test
    void doNotRankAgainInSameWeek() {
        // given
        when(noticeRankingQueryPort.existsWeeklyRankings(WEEK_START_DATE)).thenReturn(true);

        // when
        noticeWeeklyRankingService.rankWeeklyTopNotices();

        // then
        verifyNoInteractions(noticeQueryPort, noticeRankingCommandPort);
    }

    @DisplayName("이번 주 조회수가 같으면 더 최근에 게시된 공지가 높은 순위를 가진다")
    @Test
    void rankWeeklyTopNoticesOrderByPostedDateWhenTied() {
        // given
        when(noticeQueryPort.findAllViewCountsExceptDepartment()).thenReturn(List.of(
                viewCount(1L, "1", CategoryName.BACHELOR, 100, LocalDateTime.of(2026, 10, 20, 9, 0)),
                viewCount(2L, "2", CategoryName.BACHELOR, 100, LocalDateTime.of(2026, 10, 23, 9, 0))
        ));
        when(noticeRankingQueryPort.findAllViewSnapshots()).thenReturn(List.of());

        // when
        noticeWeeklyRankingService.rankWeeklyTopNotices();

        // then
        verify(noticeRankingCommandPort).saveWeeklyRankings(eq(WEEK_START_DATE), rankingsCaptor.capture());
        assertThat(rankingsCaptor.getValue())
                .extracting("ranking", "articleId")
                .containsExactly(tuple(1, "2"), tuple(2, "1"));
    }

    @DisplayName("조회수 기준값이 없으면 현재 조회수를 기준값으로 기록하며, 조회수가 0인 공지는 제외한다")
    @Test
    void initializeViewSnapshotsIfAbsent() {
        // given
        when(noticeRankingQueryPort.existsViewSnapshots()).thenReturn(false);
        when(noticeQueryPort.findAllViewCountsExceptDepartment()).thenReturn(List.of(
                viewCount(1L, "1", CategoryName.BACHELOR, 22000, LocalDateTime.of(2024, 7, 25, 9, 0)),
                viewCount(2L, "2", CategoryName.LIBRARY, 30, LocalDateTime.of(2026, 10, 26, 9, 0)),
                viewCount(3L, "3", CategoryName.STUDENT, 0, LocalDateTime.of(2026, 10, 1, 9, 0))
        ));

        // when
        noticeWeeklyRankingService.initializeViewSnapshotsIfAbsent();

        // then
        verify(noticeRankingCommandPort).replaceViewSnapshots(snapshotsCaptor.capture());
        assertThat(snapshotsCaptor.getValue())
                .extracting("categoryName", "articleId", "viewCount")
                .containsExactlyInAnyOrder(
                        tuple(CategoryName.BACHELOR, "1", 22000),
                        tuple(CategoryName.LIBRARY, "2", 30)
                );
    }

    @DisplayName("조회수 기준값이 이미 있으면 덮어쓰지 않는다")
    @Test
    void initializeViewSnapshotsWhenAlreadyExists() {
        // given
        when(noticeRankingQueryPort.existsViewSnapshots()).thenReturn(true);

        // when
        noticeWeeklyRankingService.initializeViewSnapshotsIfAbsent();

        // then
        verifyNoInteractions(noticeRankingCommandPort);
        verifyNoInteractions(noticeQueryPort);
    }

    private static NoticeViewCountDto viewCount(Long id, String articleId, CategoryName categoryName, int viewCount, LocalDateTime postedDate) {
        return new NoticeViewCountDto(id, articleId, "subject_" + articleId, categoryName, viewCount, postedDate,
                "https://www.example.com/" + articleId);
    }
}

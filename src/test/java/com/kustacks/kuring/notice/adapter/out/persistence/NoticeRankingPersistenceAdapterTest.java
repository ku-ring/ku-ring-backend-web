package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.application.port.out.NoticeRankingCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.NoticeViewSnapshot;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DisplayName("리포지토리 : NoticeRanking")
class NoticeRankingPersistenceAdapterTest extends IntegrationTestSupport {

    @Autowired
    private NoticeRankingQueryPort noticeRankingQueryPort;

    @Autowired
    private NoticeRankingCommandPort noticeRankingCommandPort;

    @DisplayName("가장 최근 주의 순위만 순위 순서대로 조회한다")
    @Test
    void findLatestWeeklyRankings() {
        // given
        LocalDate lastWeek = LocalDate.of(2026, 10, 12);
        LocalDate thisWeek = LocalDate.of(2026, 10, 19);
        noticeRankingCommandPort.saveWeeklyRankings(lastWeek, List.of(ranking(lastWeek, 1, "last_week")));
        noticeRankingCommandPort.saveWeeklyRankings(thisWeek, List.of(
                ranking(thisWeek, 2, "second"),
                ranking(thisWeek, 1, "first")
        ));

        // when
        List<NoticeWeeklyRanking> results = noticeRankingQueryPort.findLatestWeeklyRankings();

        // then
        assertThat(results)
                .extracting("weekStartDate", "ranking", "articleId")
                .containsExactly(
                        tuple(thisWeek, 1, "first"),
                        tuple(thisWeek, 2, "second")
                );
    }

    @DisplayName("주 단위로 순위가 집계되어 있는지 확인한다")
    @Test
    void existsWeeklyRankings() {
        // given
        LocalDate thisWeek = LocalDate.of(2026, 10, 19);
        noticeRankingCommandPort.saveWeeklyRankings(thisWeek, List.of(ranking(thisWeek, 1, "first")));

        // when & then
        assertThat(noticeRankingQueryPort.existsWeeklyRankings(thisWeek)).isTrue();
        assertThat(noticeRankingQueryPort.existsWeeklyRankings(thisWeek.plusWeeks(1))).isFalse();
    }

    @DisplayName("같은 주의 순위를 다시 저장하면 기존 순위를 덮어쓴다")
    @Test
    void saveWeeklyRankingsOverwriteSameWeek() {
        // given
        LocalDate thisWeek = LocalDate.of(2026, 10, 19);
        noticeRankingCommandPort.saveWeeklyRankings(thisWeek, List.of(ranking(thisWeek, 1, "before")));

        // when
        noticeRankingCommandPort.saveWeeklyRankings(thisWeek, List.of(ranking(thisWeek, 1, "after")));

        // then
        assertThat(noticeRankingQueryPort.findLatestWeeklyRankings())
                .extracting("articleId")
                .containsExactly("after");
    }

    @DisplayName("조회수 스냅샷을 교체하면 이전 스냅샷은 모두 지워진다")
    @Test
    void replaceViewSnapshots() {
        // given
        assertThat(noticeRankingQueryPort.existsViewSnapshots()).isFalse();
        noticeRankingCommandPort.replaceViewSnapshots(List.of(
                new NoticeViewSnapshot(CategoryName.BACHELOR, "1", 100)
        ));
        assertThat(noticeRankingQueryPort.existsViewSnapshots()).isTrue();

        // when
        noticeRankingCommandPort.replaceViewSnapshots(List.of(
                new NoticeViewSnapshot(CategoryName.BACHELOR, "1", 150),
                new NoticeViewSnapshot(CategoryName.LIBRARY, "2", 30)
        ));

        // then
        assertThat(noticeRankingQueryPort.findAllViewSnapshots())
                .extracting("categoryName", "articleId", "viewCount")
                .containsExactlyInAnyOrder(
                        tuple(CategoryName.BACHELOR, "1", 150),
                        tuple(CategoryName.LIBRARY, "2", 30)
                );
    }

    private NoticeWeeklyRanking ranking(LocalDate weekStartDate, int ranking, String articleId) {
        return NoticeWeeklyRanking.builder()
                .weekStartDate(weekStartDate)
                .ranking(ranking)
                .noticeId((long) ranking)
                .articleId(articleId)
                .subject("subject_" + articleId)
                .categoryName(CategoryName.BACHELOR)
                .postedDate(LocalDateTime.of(2026, 10, 20, 9, 0))
                .url("https://www.example.com/" + articleId)
                .viewCount(100)
                .build();
    }
}

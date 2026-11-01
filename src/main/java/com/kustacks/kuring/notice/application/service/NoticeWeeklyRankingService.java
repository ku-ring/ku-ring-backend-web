package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeWeeklyRankingUseCase;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeViewCountDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.NoticeViewSnapshot;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UseCase
@Transactional
@RequiredArgsConstructor
public class NoticeWeeklyRankingService implements NoticeWeeklyRankingUseCase {

    private static final int WEEKLY_TOP_NOTICE_SIZE = 5;

    private final NoticeQueryPort noticeQueryPort;
    private final NoticeRankingQueryPort noticeRankingQueryPort;
    private final NoticeRankingCommandPort noticeRankingCommandPort;
    private final Clock clock;

    @Override
    public void rankWeeklyTopNotices() {
        LocalDate weekStartDate = LocalDate.now(clock).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<NoticeViewCountDto> currentViewCounts = noticeQueryPort.findAllViewCountsExceptDepartment();
        Map<NoticeKey, Integer> previousViewCounts = noticeRankingQueryPort.findAllViewSnapshots()
                .stream()
                .collect(Collectors.toMap(
                        snapshot -> new NoticeKey(snapshot.getCategoryName(), snapshot.getArticleId()),
                        NoticeViewSnapshot::getViewCount,
                        Math::max
                ));

        List<NoticeWeeklyRanking> rankings = rank(weekStartDate, currentViewCounts, previousViewCounts);
        noticeRankingCommandPort.saveWeeklyRankings(weekStartDate, rankings);

        replaceViewSnapshots(currentViewCounts);
    }

    @Override
    public void initializeViewSnapshotsIfAbsent() {
        if (noticeRankingQueryPort.existsViewSnapshots()) {
            return;
        }

        replaceViewSnapshots(noticeQueryPort.findAllViewCountsExceptDepartment());
    }

    private void replaceViewSnapshots(List<NoticeViewCountDto> currentViewCounts) {
        // 조회수가 아직 수집되지 않은(0) 공지는 기준값으로 남기지 않는다.
        noticeRankingCommandPort.replaceViewSnapshots(
                currentViewCounts.stream()
                        .filter(notice -> notice.getViewCount() > 0)
                        .map(notice -> new NoticeViewSnapshot(notice.getCategoryName(), notice.getArticleId(), notice.getViewCount()))
                        .toList()
        );
    }

    private List<NoticeWeeklyRanking> rank(
            LocalDate weekStartDate,
            List<NoticeViewCountDto> currentViewCounts,
            Map<NoticeKey, Integer> previousViewCounts
    ) {
        LocalDateTime weekStartAt = weekStartDate.atStartOfDay();

        List<WeeklyViewCount> topNotices = currentViewCounts.stream()
                .map(notice -> new WeeklyViewCount(notice, calculateWeeklyViewCount(notice, previousViewCounts, weekStartAt)))
                .filter(weeklyViewCount -> weeklyViewCount.viewCount() > 0)
                .sorted(Comparator.comparingInt(WeeklyViewCount::viewCount).reversed()
                        .thenComparing(weeklyViewCount -> weeklyViewCount.notice().getPostedDate(), Comparator.reverseOrder()))
                .limit(WEEKLY_TOP_NOTICE_SIZE)
                .toList();

        return IntStream.range(0, topNotices.size())
                .mapToObj(index -> toRanking(weekStartDate, index + 1, topNotices.get(index)))
                .toList();
    }

    private int calculateWeeklyViewCount(
            NoticeViewCountDto notice,
            Map<NoticeKey, Integer> previousViewCounts,
            LocalDateTime weekStartAt
    ) {
        Integer previousViewCount = previousViewCounts.get(new NoticeKey(notice.getCategoryName(), notice.getArticleId()));
        if (previousViewCount != null) {
            return Math.max(notice.getViewCount() - previousViewCount, 0);
        }

        // 지난 스냅샷에 없는 공지는 이번 주에 게시된 경우에만 누적 조회수 전체를 이번 주 조회수로 본다.
        if (!notice.getPostedDate().isBefore(weekStartAt)) {
            return notice.getViewCount();
        }

        return 0;
    }

    private NoticeWeeklyRanking toRanking(LocalDate weekStartDate, int ranking, WeeklyViewCount weeklyViewCount) {
        NoticeViewCountDto notice = weeklyViewCount.notice();
        return NoticeWeeklyRanking.builder()
                .weekStartDate(weekStartDate)
                .ranking(ranking)
                .noticeId(notice.getId())
                .articleId(notice.getArticleId())
                .subject(notice.getSubject())
                .categoryName(notice.getCategoryName())
                .postedDate(notice.getPostedDate())
                .url(notice.getUrl())
                .viewCount(weeklyViewCount.viewCount())
                .build();
    }

    private record NoticeKey(CategoryName categoryName, String articleId) {
    }

    private record WeeklyViewCount(NoticeViewCountDto notice, int viewCount) {
    }
}

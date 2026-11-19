package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingQueryPort;
import com.kustacks.kuring.notice.domain.NoticeViewSnapshot;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class NoticeRankingPersistenceAdapter implements NoticeRankingQueryPort, NoticeRankingCommandPort {

    private final NoticeWeeklyRankingRepository noticeWeeklyRankingRepository;
    private final NoticeViewSnapshotRepository noticeViewSnapshotRepository;
    private final NoticeJdbcRepository noticeJdbcRepository;

    @Override
    public List<NoticeViewSnapshot> findAllViewSnapshots() {
        return noticeViewSnapshotRepository.findAll();
    }

    @Override
    public boolean existsViewSnapshots() {
        return noticeViewSnapshotRepository.count() > 0;
    }

    @Override
    public List<NoticeWeeklyRanking> findLatestWeeklyRankings() {
        return noticeWeeklyRankingRepository.findTopByOrderByWeekStartDateDesc()
                .map(latest -> noticeWeeklyRankingRepository.findAllByWeekStartDateOrderByRankingAsc(latest.getWeekStartDate()))
                .orElse(Collections.emptyList());
    }

    @Override
    public boolean existsWeeklyRankings(LocalDate weekStartDate) {
        return noticeWeeklyRankingRepository.existsByWeekStartDate(weekStartDate);
    }

    @Transactional
    @Override
    public void saveWeeklyRankings(LocalDate weekStartDate, List<NoticeWeeklyRanking> rankings) {
        noticeWeeklyRankingRepository.deleteAllByWeekStartDate(weekStartDate);
        noticeWeeklyRankingRepository.saveAll(rankings);
    }

    @Transactional
    @Override
    public void replaceViewSnapshots(List<NoticeViewSnapshot> snapshots) {
        noticeViewSnapshotRepository.deleteAllInBatch();
        noticeJdbcRepository.saveAllViewSnapshots(snapshots);
    }
}

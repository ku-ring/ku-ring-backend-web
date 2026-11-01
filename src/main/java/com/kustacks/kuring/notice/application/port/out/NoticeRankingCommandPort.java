package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.domain.NoticeViewSnapshot;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;

import java.time.LocalDate;
import java.util.List;

public interface NoticeRankingCommandPort {

    void saveWeeklyRankings(LocalDate weekStartDate, List<NoticeWeeklyRanking> rankings);

    void replaceViewSnapshots(List<NoticeViewSnapshot> snapshots);
}

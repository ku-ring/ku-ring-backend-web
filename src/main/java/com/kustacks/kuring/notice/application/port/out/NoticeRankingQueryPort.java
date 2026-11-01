package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.domain.NoticeViewSnapshot;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;

import java.util.List;

public interface NoticeRankingQueryPort {

    List<NoticeViewSnapshot> findAllViewSnapshots();

    boolean existsViewSnapshots();

    List<NoticeWeeklyRanking> findLatestWeeklyRankings();
}

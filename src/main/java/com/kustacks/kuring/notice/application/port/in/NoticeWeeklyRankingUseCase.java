package com.kustacks.kuring.notice.application.port.in;

public interface NoticeWeeklyRankingUseCase {
    void rankWeeklyTopNotices();

    void initializeViewSnapshotsIfAbsent();
}

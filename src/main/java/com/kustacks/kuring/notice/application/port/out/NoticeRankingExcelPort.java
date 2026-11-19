package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;

import java.time.LocalDate;
import java.util.List;

public interface NoticeRankingExcelPort {

    byte[] createWeeklyRankingExcel(LocalDate weekStartDate, List<NoticeWeeklyRanking> rankings);
}

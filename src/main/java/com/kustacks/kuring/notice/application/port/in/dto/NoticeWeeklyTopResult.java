package com.kustacks.kuring.notice.application.port.in.dto;

import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;

public record NoticeWeeklyTopResult(
        Long id,
        String articleId,
        String subject,
        String category,
        String categoryKorName,
        Integer viewCount,
        String postedDate,
        String url
) {
    public static NoticeWeeklyTopResult from(NoticeWeeklyRanking ranking) {
        return new NoticeWeeklyTopResult(
                ranking.getNoticeId(),
                ranking.getArticleId(),
                ranking.getSubject(),
                ranking.getCategoryName().getName(),
                ranking.getCategoryName().getKorName(),
                ranking.getViewCount(),
                ranking.getPostedDate().toLocalDate().toString(),
                ranking.getUrl()
        );
    }
}

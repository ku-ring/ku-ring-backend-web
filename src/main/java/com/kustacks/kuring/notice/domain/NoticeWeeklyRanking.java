package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 한 주(월요일 ~ 일요일) 동안 조회수가 가장 많이 늘어난 공지의 순위를 집계 시점의 공지 정보와 함께 고정 저장한다
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeWeeklyRanking extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "ranking", nullable = false)
    private Integer ranking;

    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    @Column(name = "article_id", nullable = false, length = 32)
    private String articleId;

    @Column(name = "subject", nullable = false, length = 128)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_name", nullable = false, length = 32)
    private CategoryName categoryName;

    @Column(name = "posted_dt", nullable = false)
    private LocalDateTime postedDate;

    @Column(name = "url", nullable = false, length = 256)
    private String url;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Builder
    public NoticeWeeklyRanking(
            LocalDate weekStartDate, Integer ranking, Long noticeId, String articleId, String subject,
            CategoryName categoryName, LocalDateTime postedDate, String url, Integer viewCount
    ) {
        this.weekStartDate = weekStartDate;
        this.ranking = ranking;
        this.noticeId = noticeId;
        this.articleId = articleId;
        this.subject = subject;
        this.categoryName = categoryName;
        this.postedDate = postedDate;
        this.url = url;
        this.viewCount = viewCount;
    }
}

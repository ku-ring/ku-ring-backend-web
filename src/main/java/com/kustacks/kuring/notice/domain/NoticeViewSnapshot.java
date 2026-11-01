package com.kustacks.kuring.notice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주간 조회수를 계산하기 위해, 직전 주간 순위 집계 시점의 공지별 누적 조회수를 기록한다
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeViewSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_name", nullable = false, length = 32)
    private CategoryName categoryName;

    @Column(name = "article_id", nullable = false, length = 32)
    private String articleId;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    public NoticeViewSnapshot(CategoryName categoryName, String articleId, Integer viewCount) {
        this.categoryName = categoryName;
        this.articleId = articleId;
        this.viewCount = viewCount;
    }
}

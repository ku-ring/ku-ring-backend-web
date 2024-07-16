package com.kustacks.kuring.notice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

    @Id
    @Getter(AccessLevel.PROTECTED)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "article_id", length = 32, nullable = false)
    private String articleId;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "subject", length = 128, nullable = false)
    private String subject;

    @Column(name = "important")
    private Boolean important = Boolean.FALSE;

    @Column(name = "embedded")
    private Boolean embedded = Boolean.FALSE;

    @Embedded
    protected NoticeDateTime noticeDateTime;

    @Embedded
    private Url url;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_name", nullable = false)
    private CategoryName categoryName;

    public Notice(String articleId, String postedDate, String updatedDate,
                  String subject, CategoryName categoryName, Boolean important,
                  String fullUrl) {
        this.articleId = articleId;
        this.subject = subject;
        this.categoryName = categoryName;
        this.important = important;
        this.noticeDateTime = new NoticeDateTime(postedDate, updatedDate);
        this.url = new Url(fullUrl);
    }

    public boolean isImportant() {
        return this.important;
    }

    public String getCategoryName() {
        return this.categoryName.getName();
    }

    public String getCategoryKoreaName() {
        return this.categoryName.getKorName();
    }

    public String getUrl() {
        return this.url.getValue();
    }

    public String getPostedDate() {
        return this.noticeDateTime.postedDateStr();
    }

    public String getUpdatedDate() {
        return this.noticeDateTime.updatedDateStr();
    }

    public void embeddedSuccess() {
        this.embedded = Boolean.TRUE;
    }

    public boolean isEmbedded() {
        return this.embedded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return Objects.equals(getId(), notice.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}


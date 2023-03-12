package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "article_id", length = 15, nullable = false)
    private String articleId;

    @Column(name = "posted_dt", length = 32, nullable = false)
    private String postedDate;

    @Column(name = "updated_dt", length = 32)
    private String updatedDate;

    @Column(name = "subject", length = 128, nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_name", nullable = false)
    private Category category;

    public Notice(String articleId, String postedDate, String updatedDate, String subject, Category category) {
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.updatedDate = updatedDate;
        this.subject = subject;
        this.category = category;
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

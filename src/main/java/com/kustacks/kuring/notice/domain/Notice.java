package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "article_id", length = 15, nullable = false)
    private String articleId;

    @Column(name = "posted_dt", length = 32, nullable = false)
    private String postedDate;

    @Column(name = "updated_dt", length = 32, nullable = true)
    private String updatedDate;

    @Column(name = "subject", length = 128, nullable = false)
    private String subject;

    @ManyToOne
    @JoinColumn(name = "category_name", nullable = false)
    private Category category;

    @Builder
    public Notice(String articleId, String postedDate, String updatedDate, String subject, Category category) {
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.updatedDate = updatedDate;
        this.subject = subject;
        this.category = category;
    }
}

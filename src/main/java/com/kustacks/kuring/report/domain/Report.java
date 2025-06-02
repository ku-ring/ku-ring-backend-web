package com.kustacks.kuring.report.domain;

import com.kustacks.kuring.common.domain.BaseTimeEntity;
import com.kustacks.kuring.common.domain.Content;
import com.kustacks.kuring.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {

    @Getter()
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Getter()
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Embedded
    private Content content;

    @Column(name = "target_type", nullable = false, length = 30)
    @Enumerated(value = EnumType.STRING)
    private ReportTargetType targetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    public Report(Long targetId, String content, User reporter, ReportTargetType targetType) {
        this.targetId = targetId;
        this.content = new Content(content);
        this.reporter = reporter;
        this.targetType = targetType;
    }

    public String getContent() {
        return content.getValue();
    }

    public Long getUserId() {
        return reporter.getId();
    }

    public String getTargetType() {
        return targetType.name();
    }
}

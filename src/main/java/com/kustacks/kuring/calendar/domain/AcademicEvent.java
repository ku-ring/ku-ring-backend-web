package com.kustacks.kuring.calendar.domain;

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
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity(name = "academic_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcademicEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_uid", nullable = false, updatable = false, length = 255)
    private String eventUid; // 이벤트 고유 ID

    @Column(name = "summary", nullable = false, length = 255)
    private String summary; // 이벤트 제목

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 이벤트 설명

    //TODO: 카테고리 확정 시 ENUM화 필요 @김한주 25.08.24
    @Column(name = "category", length = 20)
    private String category; // 일정 분류 (예: 학사, 시험, 휴강 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "transparent", nullable = false, length = 20)
    private Transparent transparent; // 이벤트 바쁨 정도 (OPAQUE/TRANSPARENT)

    @Column(name = "sequence", nullable = false)
    private Integer sequence; // 이벤트 변경 횟수(초기 0, 동기화용)

    @Column(name = "notify_enabled", nullable = false)
    private Boolean notifyEnabled; // true = 알림 발송 대상, false = 저장만

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // 시작일시

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // 종료일시

    @Builder
    private AcademicEvent(String eventUid, String summary, String description, String category,
                         Transparent transparent, Integer sequence, Boolean notifyEnabled,
                         LocalDateTime startTime, LocalDateTime endTime) {
        this.eventUid = eventUid;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.transparent = transparent;
        this.sequence = sequence;
        this.notifyEnabled = notifyEnabled;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static AcademicEvent from(String uid, String summary, String description, String category,
                                     Transparent transparent, Integer sequence, Boolean notifyEnabled,
                                     LocalDateTime startTime, LocalDateTime endTime) {
        Assert.notNull(uid, "UID must not be null");
        Assert.notNull(summary, "Summary must not be null");
        Assert.notNull(category, "Category must not be null");
        Assert.notNull(transparent, "Transparent must not be null");
        Assert.notNull(sequence, "Sequence must not be null");
        Assert.notNull(startTime, "Start time must not be null");
        Assert.notNull(endTime, "End time must not be null");

        return AcademicEvent.builder()
                .eventUid(uid)
                .summary(summary)
                .description(description)
                .category(category)
                .transparent(transparent)
                .sequence(sequence)
                .notifyEnabled(notifyEnabled)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public void update(AcademicEvent newEvent) {
        Assert.notNull(newEvent, "New event must not be null");
        Assert.isTrue(Objects.equals(this.eventUid, newEvent.eventUid), "Event UID must be the same");
        Assert.isTrue(newEvent.sequence >= this.sequence, "New sequence must be greater than or equal to current sequence");

        this.summary = newEvent.summary;
        this.description = newEvent.description;
        this.category = newEvent.category;
        this.transparent = newEvent.transparent;
        this.sequence = newEvent.sequence;
        this.notifyEnabled = newEvent.notifyEnabled;
        this.startTime = newEvent.startTime;
        this.endTime = newEvent.endTime;
    }
}
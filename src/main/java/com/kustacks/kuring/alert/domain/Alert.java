package com.kustacks.kuring.alert.domain;

import com.google.firebase.database.annotations.NotNull;
import com.kustacks.kuring.common.domain.BaseTimeEntity;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "title", length = 128, nullable = false)
    private String title;

    @Column(name = "content", length = 256)
    private String content;

    @NotNull
    @Column(name = "alert_time", nullable = false)
    private LocalDateTime alertTime;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AlertStatus status = AlertStatus.PENDING;

    private Alert(String title, String content, LocalDateTime alertTime) {
        this.title = title;
        this.content = content;
        this.alertTime = alertTime;
    }

    public static Alert create(
            String title,
            String content,
            LocalDateTime wakeTime,
            LocalDateTime currentTime
    ) {
        if (currentTime.isAfter(wakeTime) || currentTime.isEqual(wakeTime)) {
            throw new IllegalArgumentException(ErrorCode.DOMAIN_CANNOT_CREATE.getMessage());
        }
        return new Alert(title, content, wakeTime);
    }

    public void changeRequested() {
        validateNotPending();
        this.status = AlertStatus.REQUESTED;
    }

    private void validateNotPending() {
        if (status != AlertStatus.PENDING) {
            throw new IllegalStateException(ErrorCode.DOMAIN_CANNOT_CREATE.getMessage());
        }
    }
}

package com.kustacks.kuring.building.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingHours {

    @Enumerated(EnumType.STRING)
    @Column(name = "period", length = 20, nullable = false)
    private OperatingPeriod period;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_group", length = 20, nullable = false)
    private OperatingDayGroup dayGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OperatingHoursStatus status;

    @Column(name = "opens_at")
    private LocalTime opensAt;

    @Column(name = "closes_at")
    private LocalTime closesAt;

    public OperatingHours(
            OperatingPeriod period,
            OperatingDayGroup dayGroup,
            OperatingHoursStatus status,
            LocalTime opensAt,
            LocalTime closesAt
    ) {
        validateRequiredFields(period, dayGroup, status);
        validateTimes(status, opensAt, closesAt);

        this.period = period;
        this.dayGroup = dayGroup;
        this.status = status;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    public boolean matches(OperatingPeriod period, OperatingDayGroup dayGroup) {
        return this.period == period && this.dayGroup == dayGroup;
    }

    private static void validateRequiredFields(
            OperatingPeriod period,
            OperatingDayGroup dayGroup,
            OperatingHoursStatus status
    ) {
        if (period == null) {
            throw new IllegalArgumentException("운영 기간은 필수입니다.");
        }
        if (dayGroup == null) {
            throw new IllegalArgumentException("운영 요일 구분은 필수입니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("운영시간 상태는 필수입니다.");
        }
    }

    private static void validateTimes(
            OperatingHoursStatus status,
            LocalTime opensAt,
            LocalTime closesAt
    ) {
        if (status == OperatingHoursStatus.SCHEDULED) {
            if (opensAt == null) {
                throw new IllegalArgumentException("지정 운영시간에는 시작 시간이 필요합니다.");
            }
            if (closesAt == null) {
                throw new IllegalArgumentException("지정 운영시간에는 종료 시간이 필요합니다.");
            }
            return;
        }

        if (opensAt != null) {
            throw new IllegalArgumentException("지정 운영시간이 아닌 경우 시작 시간을 입력할 수 없습니다.");
        }
        if (closesAt != null) {
            throw new IllegalArgumentException("지정 운영시간이 아닌 경우 종료 시간을 입력할 수 없습니다.");
        }
    }
}

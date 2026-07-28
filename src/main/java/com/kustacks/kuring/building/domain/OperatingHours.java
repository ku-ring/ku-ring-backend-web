package com.kustacks.kuring.building.domain;

import com.kustacks.kuring.common.exception.code.ErrorCode;
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
            throw new IllegalArgumentException(ErrorCode.OPERATING_PERIOD_REQUIRED.getMessage());
        }
        if (dayGroup == null) {
            throw new IllegalArgumentException(ErrorCode.OPERATING_DAY_GROUP_REQUIRED.getMessage());
        }
        if (status == null) {
            throw new IllegalArgumentException(ErrorCode.OPERATING_HOURS_STATUS_REQUIRED.getMessage());
        }
    }

    private static void validateTimes(
            OperatingHoursStatus status,
            LocalTime opensAt,
            LocalTime closesAt
    ) {
        if (status == OperatingHoursStatus.SCHEDULED) {
            if (opensAt == null) {
                throw new IllegalArgumentException(ErrorCode.OPERATING_HOURS_OPEN_TIME_REQUIRED.getMessage());
            }
            if (closesAt == null) {
                throw new IllegalArgumentException(ErrorCode.OPERATING_HOURS_CLOSE_TIME_REQUIRED.getMessage());
            }
            return;
        }

        if (opensAt != null) {
            throw new IllegalArgumentException(ErrorCode.OPERATING_HOURS_OPEN_TIME_NOT_ALLOWED.getMessage());
        }
        if (closesAt != null) {
            throw new IllegalArgumentException(ErrorCode.OPERATING_HOURS_CLOSE_TIME_NOT_ALLOWED.getMessage());
        }
    }
}

package com.kustacks.kuring.club.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public enum ClubRecruitmentStatus {

    ALWAYS("always"),
    BEFORE("before"),
    RECRUITING("recruiting"),
    CLOSED("closed");

    private final String value;

    ClubRecruitmentStatus(String value) {
        this.value = value;
    }

    public static ClubRecruitmentStatus from(
            LocalDateTime start,
            LocalDateTime end,
            Boolean isAlways,
            LocalDateTime now
    ) {
        if (Boolean.TRUE.equals(isAlways)) {
            return ALWAYS;
        }

        if (start != null && now.isBefore(start)) {
            return BEFORE;
        }

        if (end != null && now.isAfter(end)) {
            return CLOSED;
        }

        return RECRUITING;
    }
}
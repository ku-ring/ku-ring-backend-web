package com.kustacks.kuring.club.domain;

import lombok.Getter;

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
}
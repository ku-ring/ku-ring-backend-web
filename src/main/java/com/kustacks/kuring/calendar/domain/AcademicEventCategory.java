package com.kustacks.kuring.calendar.domain;

/**
 * 학사일정 카테고리를 나타내는 순수 enum
 * 비즈니스 로직은 제거하고 카테고리 정의만 담당
 */
public enum AcademicEventCategory {

    ACADEMIC_DEGREE("학적/학위"),
    REGISTRATION_COURSE_GRADE("등록/수강/성적"),
    ACADEMIC_OPERATION_EVENT("학사 운영/행사"),
    ETC("기타");

    private final String displayName;

    AcademicEventCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
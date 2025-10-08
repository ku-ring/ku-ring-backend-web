package com.kustacks.kuring.calendar.domain;

public enum AcademicEventCategory {

    ACADEMIC_DEGREE("학적/학위"),
    REGISTRATION_COURSE_GRADE("등록/수강/성적"),
    ACADEMIC_OPERATION_EVENT("학사운영/행사"),
    ETC("기타");

    private final String displayName;

    AcademicEventCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
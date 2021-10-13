package com.kustacks.kuring.kuapi;

public enum NoticeCategory {

    BACHELOR("bachelor"),
    SCHOLARSHIP("scholarship"),
    EMPLOYMENT("employment"),
    NATIONAL("national"),
    STUDENT("student"),
    INDUSTRY_UNIV("industry_university"),
    NORMAL("normal");

    private String name;

    NoticeCategory(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.name;
    }
}

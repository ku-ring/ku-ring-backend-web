package com.kustacks.kuring.kuapi;

import lombok.Getter;

@Getter
public enum NoticeCategory {

    BACHELOR("bachelor", "bch"),
    SCHOLARSHIP("scholarship", "sch"),
    EMPLOYMENT("employment", "emp"),
    NATIONAL("national", "nat"),
    STUDENT("student", "stu"),
    INDUSTRY_UNIV("industry_university", "ind"),
    NORMAL("normal", "nor"),
    LIBRARY("library", "lib");

    private String name;
    private String shortName;

    NoticeCategory(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }
}

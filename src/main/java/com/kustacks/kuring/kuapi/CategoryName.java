package com.kustacks.kuring.kuapi;

import lombok.Getter;

@Getter
public enum CategoryName {

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

    CategoryName(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }
}

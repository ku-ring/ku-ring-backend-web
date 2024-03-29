package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kustacks.kuring.common.exception.code.ErrorCode.CAT_NOT_EXIST_CATEGORY;

@Getter
public enum CategoryName {

    BACHELOR("bachelor", "bch", "학사"),
    SCHOLARSHIP("scholarship", "sch", "장학"),
    LIBRARY("library", "lib", "도서관"),
    EMPLOYMENT("employment", "emp", "취창업"),
    NATIONAL("national", "nat", "국제"),
    STUDENT("student", "stu", "학생"),
    INDUSTRY_UNIVERSITY("industry_university", "ind", "산학"),
    NORMAL("normal", "nor", "일반"),
    DEPARTMENT("department", "dep", "학과");

    private static final Map<String, String> NAME_MAP;

    static {
        NAME_MAP = Collections.unmodifiableMap(Arrays.stream(CategoryName.values())
                .collect(Collectors.toMap(CategoryName::getName, CategoryName::name)));
    }

    private String name;
    private String shortName;
    private String korName;

    CategoryName(String name, String shortName, String korName) {
        this.name = name;
        this.shortName = shortName;
        this.korName = korName;
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameShortName(String shortName) {
        return this.shortName.equals(shortName);
    }

    public boolean isSameKorName(String korName) {
        return this.korName.equals(korName);
    }

    public static CategoryName fromStringName(String name) {
        String findName = Optional.ofNullable(NAME_MAP.get(name))
                .orElseThrow(() -> new NotFoundException(CAT_NOT_EXIST_CATEGORY));
        return CategoryName.valueOf(findName);
    }
}

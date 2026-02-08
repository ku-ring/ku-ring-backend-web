package com.kustacks.kuring.club.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kustacks.kuring.common.exception.code.ErrorCode.CAT_NOT_EXIST_CLUB_CATEGORY;

@Getter
public enum ClubCategory {

    ACADEMIC("academic", "학술활동"),
    CULTURE_ART("culture_art", "문화예술"),
    SOCIAL_VALUE("social_value", "사회가치"),
    ACTIVITY("activity", "야외활동");

    private static final Map<String, String> NAME_MAP;

    static {
        NAME_MAP = Collections.unmodifiableMap(Arrays.stream(ClubCategory.values())
                .collect(Collectors.toMap(ClubCategory::getName, ClubCategory::name))
        );
    }

    private final String name;
    private final String korName;

    ClubCategory(String name, String korName) {
        this.name = name;
        this.korName = korName;
    }

    public static ClubCategory fromName(String name) {
        String findName = Optional.ofNullable(NAME_MAP.get(name))
                .orElseThrow(() -> new NotFoundException(CAT_NOT_EXIST_CLUB_CATEGORY));
        return ClubCategory.valueOf(findName);
    }
}

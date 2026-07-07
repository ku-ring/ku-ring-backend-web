package com.kustacks.kuring.club.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kustacks.kuring.common.exception.code.ErrorCode.CLUB_DIVISION_NOT_SUPPORTED;

@Getter
public enum ClubDivision {

    CENTRAL("central", "중앙"),
    LIBERAL_ARTS("liberal_arts", "문과대학"),
    SCIENCE("science", "이과대학"),
    ARCHITECTURE("architecture", "건축대학"),
    ENGINEERING("engineering", "공과대학"),
    SOCIAL_SCIENCES("social_sciences", "사회과학대학"),
    BUSINESS("business", "경영대학"),
    REAL_ESTATE("real_estate", "부동산과학원"),
    KU_CONVERGENCE("ku_convergence", "KU융합과학기술원"),
    SANGHUH_LIFE_SCIENCE("sanghuh_life_science", "상허생명과학대학"),
    VETERINARY("veterinary", "수의과대학"),
    ART_DESIGN("art_design", "예술디자인대학"),
    EDUCATION("education", "사범대학"),
    SANGHUH_GENERAL("sanghuh_general", "상허교양대학"),
    INTERNATIONAL("international", "국제대학"),
    CONVERGENCE_SCI_TECH("convergence_sci_tech", "융합과학기술원"),
    LIFE_SCIENCE("life_science", "생명과학대학"),
    ETC("etc", "기타");

    private static final Map<String, String> NAME_MAP;

    static {
        NAME_MAP = Collections.unmodifiableMap(Arrays.stream(values())
                .collect(Collectors.toMap(ClubDivision::getName, ClubDivision::name))
        );
    }

    private final String name;
    private final String korName;

    ClubDivision(String name, String korName) {
        this.name = name;
        this.korName = korName;
    }

    public static ClubDivision fromName(String name) {
        String findName = Optional.ofNullable(NAME_MAP.get(name))
                .orElseThrow(() -> new NotFoundException(CLUB_DIVISION_NOT_SUPPORTED));
        return ClubDivision.valueOf(findName);
    }
}

package com.kustacks.kuring.club.application.port.in.dto;

import com.kustacks.kuring.club.domain.ClubDivision;

public record ClubDivisionResult(
        String code,
        String koreanName
) {
    public static ClubDivisionResult from(ClubDivision division) {
        return new ClubDivisionResult(
                division.getName(),
                division.getKorName()
        );
    }
}

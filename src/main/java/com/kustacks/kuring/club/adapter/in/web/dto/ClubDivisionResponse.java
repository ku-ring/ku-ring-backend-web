package com.kustacks.kuring.club.adapter.in.web.dto;

import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;

public record ClubDivisionResponse(
        String code,
        String koreanName
) {
    public static ClubDivisionResponse from(ClubDivisionResult result) {
        return new ClubDivisionResponse(
                result.code(),
                result.koreanName()
        );
    }
}

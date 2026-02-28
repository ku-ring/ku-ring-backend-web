package com.kustacks.kuring.club.adapter.in.web.dto;

import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;

import java.util.List;

public record ClubDivisionListResponse(
        List<ClubDivisionResult> divisions
) {
    public static ClubDivisionListResponse from(List<ClubDivisionResult> results) {
        return new ClubDivisionListResponse(results);
    }
}

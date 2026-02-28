package com.kustacks.kuring.club.adapter.in.web.dto;

import com.kustacks.kuring.club.application.port.in.dto.ClubItemResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;

import java.util.List;

public record ClubListResponse(
        List<ClubItemResult> clubs
) {

    public static ClubListResponse from(ClubListResult result) {
        return new ClubListResponse(result.clubs());
    }
}

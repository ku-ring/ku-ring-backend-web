package com.kustacks.kuring.club.adapter.in.web.dto;

import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;

import java.util.List;

public record ClubListResponse(
        List<ClubItemResponse> clubs,
        String cursor,
        boolean hasNext,
        int totalCount
) {

    public static ClubListResponse from(ClubListResult result) {
        List<ClubItemResponse> clubs = result.clubs().stream()
                .map(ClubItemResponse::from)
                .toList();

        return new ClubListResponse(
                clubs,
                result.cursor(),
                result.hasNext(),
                result.totalCount()
        );
    }
}

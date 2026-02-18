package com.kustacks.kuring.club.adapter.in.web.dto;

import com.kustacks.kuring.club.application.port.in.dto.ClubItemResult;

import java.time.LocalDateTime;

public record ClubItemResponse(
        Long id,
        String name,
        String summary,
        String iconImageUrl,
        String category,
        String division,
        boolean isSubscribed,
        int subscriberCount,
        LocalDateTime recruitStartDate,
        LocalDateTime recruitEndDate
) {
    public static ClubItemResponse from(ClubItemResult result) {
        return new ClubItemResponse(
                result.id(),
                result.name(),
                result.summary(),
                result.iconImageUrl(),
                result.category(),
                result.division(),
                result.isSubscribed(),
                result.subscriberCount(),
                result.recruitStartDate(),
                result.recruitEndDate()
        );
    }
}

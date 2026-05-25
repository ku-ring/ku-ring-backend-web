package com.kustacks.kuring.club.application.port.in.dto;

import java.time.LocalDateTime;

public record ClubItemResult(
        Long id,
        String name,
        String summary,
        String iconImageUrl,
        String category,
        String division,
        boolean isSubscribed,
        Long subscriberCount,
        LocalDateTime recruitStartDate,
        LocalDateTime recruitEndDate,
        String recruitmentStatus
) {
}

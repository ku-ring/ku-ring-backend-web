package com.kustacks.kuring.club.application.port.in.dto;

import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;

import java.time.LocalDateTime;

public record ClubItemResult(
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
    public static ClubItemResult from(
            ClubReadModel clubReadModel,
            boolean isSubscribed,
            int subscriberCount
    ) {
        return new ClubItemResult(
                clubReadModel.getId(),
                clubReadModel.getName(),
                clubReadModel.getSummary(),
                clubReadModel.getIconImageUrl(),
                clubReadModel.getCategory().getName(),
                clubReadModel.getDivision().getName(),
                isSubscribed,
                subscriberCount,
                clubReadModel.getRecruitStartDate(),
                clubReadModel.getRecruitEndDate()
        );
    }
}

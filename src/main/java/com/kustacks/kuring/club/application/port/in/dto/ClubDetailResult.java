package com.kustacks.kuring.club.application.port.in.dto;

import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.club.domain.ClubRecruitmentStatus;

import java.time.LocalDateTime;

public record ClubDetailResult(
        Long id,
        String name,
        String summary,
        ClubCategory category,
        ClubDivision division,
        int subscriberCount,
        boolean isSubscribed,
        String instagramUrl,
        String youtubeUrl,
        String etcUrl,
        String description,
        String qualifications,
        ClubRecruitmentStatus recruitmentStatus,
        LocalDateTime recruitStartAt,
        LocalDateTime recruitEndAt,
        String applyUrl,
        String posterImageUrl,
        Location location
) {
    public record Location(
            String building,
            String room,
            Double lon,
            Double lat
    ) {
    }
}

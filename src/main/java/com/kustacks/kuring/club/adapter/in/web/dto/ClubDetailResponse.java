package com.kustacks.kuring.club.adapter.in.web.dto;

import com.kustacks.kuring.club.application.port.in.dto.ClubDetailResult;

import java.time.LocalDateTime;

public record ClubDetailResponse(
        Long id,
        String name,
        String summary,
        String category,
        String division,
        int subscriberCount,
        boolean isSubscribed,
        String instagramUrl,
        String youtubeUrl,
        String etcUrl,
        String description,
        String qualifications,
        String recruitmentStatus,
        LocalDateTime recruitStartAt,
        LocalDateTime recruitEndAt,
        String applyUrl,
        String posterImageUrl,
        Location location
) {

    public static ClubDetailResponse from(ClubDetailResult result) {
        return new ClubDetailResponse(
                result.id(),
                result.name(),
                result.summary(),
                result.category().getName(),
                result.division().getName(),
                result.subscriberCount(),
                result.isSubscribed(),
                result.instagramUrl(),
                result.youtubeUrl(),
                result.etcUrl(),
                result.description(),
                result.qualifications(),
                result.recruitmentStatus().getValue(),
                result.recruitStartAt(),
                result.recruitEndAt(),
                result.applyUrl(),
                result.posterImageUrl(),
                Location.from(result.location())
        );
    }

    public record Location(
            String building,
            String room,
            Double lon,
            Double lat
    ) {
        public static Location from(ClubDetailResult.Location location) {
            if (location == null) return null;

            return new Location(
                    location.building(),
                    location.room(),
                    location.lon(),
                    location.lat()
            );
        }
    }
}

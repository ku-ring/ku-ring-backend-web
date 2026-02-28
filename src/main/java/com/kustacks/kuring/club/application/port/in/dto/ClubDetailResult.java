package com.kustacks.kuring.club.application.port.in.dto;

import com.kustacks.kuring.club.application.port.out.dto.ClubDetailDto;
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
        Long subscriberCount,
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

    public static ClubDetailResult from(
            ClubDetailDto clubDetailDto,
            Long subscriberCount,
            boolean isSubscribed,
            ClubRecruitmentStatus recruitmentStatus
    ) {
        return new ClubDetailResult(
                clubDetailDto.getId(),
                clubDetailDto.getName(),
                clubDetailDto.getSummary(),
                clubDetailDto.getCategory(),
                clubDetailDto.getDivision(),
                subscriberCount,
                isSubscribed,
                clubDetailDto.getInstagramUrl(),
                clubDetailDto.getYoutubeUrl(),
                clubDetailDto.getEtcUrl(),
                clubDetailDto.getDescription(),
                clubDetailDto.getQualifications(),
                recruitmentStatus,
                clubDetailDto.getRecruitStartAt(),
                clubDetailDto.getRecruitEndAt(),
                clubDetailDto.getApplyUrl(),
                clubDetailDto.getPosterImagePath(),
                Location.from(clubDetailDto)
        );
    }

    public record Location(
            String building,
            String room,
            Double lon,
            Double lat
    ) {
        public static Location from(ClubDetailDto clubDetailDto) {
            if (!clubDetailDto.hasLocation()) {
                return null;
            }

            return new Location(
                    clubDetailDto.getBuilding(),
                    clubDetailDto.getRoom(),
                    clubDetailDto.getLon(),
                    clubDetailDto.getLat()
            );
        }
    }
}

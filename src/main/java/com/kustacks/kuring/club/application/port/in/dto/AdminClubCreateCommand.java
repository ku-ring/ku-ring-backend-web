package com.kustacks.kuring.club.application.port.in.dto;

import org.springframework.web.multipart.MultipartFile;

public record AdminClubCreateCommand(
        String name,
        String summary,
        String description,
        String category,
        String division,
        boolean isAlways,
        String recruitStartAt,
        String recruitEndAt,
        String applyUrl,
        String qualifications,
        String instagramUrl,
        String youtubeUrl,
        String etcUrl,
        String building,
        String room,
        Double lat,
        Double lon,
        MultipartFile iconImage,
        MultipartFile posterImage
) {
}

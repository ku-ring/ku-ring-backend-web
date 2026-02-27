package com.kustacks.kuring.admin.adapter.in.web.dto;

import com.kustacks.kuring.club.application.port.in.dto.AdminClubCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record AdminClubCreateRequest(
        @NotBlank @Size(max = 30) String name,
        @NotBlank @Size(max = 30) String summary,
        String description,
        @NotBlank String category,
        @NotBlank String division,
        @NotNull Boolean isAlways,
        String recruitStartAt,
        String recruitEndAt,
        @Size(max = 255) String applyUrl,
        String qualifications,
        String instagramUrl,
        String youtubeUrl,
        String etcUrl,
        @Size(max = 30) String building,
        @Size(max = 30) String room,
        Double lat,
        Double lon
) {
    public AdminClubCreateCommand toCommand(MultipartFile logoImage, MultipartFile posterImage) {
        return new AdminClubCreateCommand(
                name,
                summary,
                description,
                category,
                division,
                isAlways,
                recruitStartAt,
                recruitEndAt,
                applyUrl,
                qualifications,
                instagramUrl,
                youtubeUrl,
                etcUrl,
                building,
                room,
                lat,
                lon,
                logoImage,
                posterImage
        );
    }
}

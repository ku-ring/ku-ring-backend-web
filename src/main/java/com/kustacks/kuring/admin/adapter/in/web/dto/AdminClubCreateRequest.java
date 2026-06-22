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
        Long buildingId,
        @Size(max = 30) String room
) {
    public AdminClubCreateCommand toCommand(MultipartFile iconImage, MultipartFile posterImage) {
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
                buildingId,
                room,
                iconImage,
                posterImage
        );
    }
}

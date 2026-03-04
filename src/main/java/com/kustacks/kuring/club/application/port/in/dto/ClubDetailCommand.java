package com.kustacks.kuring.club.application.port.in.dto;

public record ClubDetailCommand(
        Long clubId,
        String email
) {
}

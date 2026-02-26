package com.kustacks.kuring.club.application.port.in.dto;

public record ClubSubscriptionCommand(
        String email,
        Long clubId
) {
}

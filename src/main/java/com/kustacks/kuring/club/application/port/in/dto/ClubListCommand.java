package com.kustacks.kuring.club.application.port.in.dto;

import java.util.Arrays;
import java.util.List;

public record ClubListCommand(
        String category,
        String division,
        String email
) {
    public List<String> divisionList() {
        if (division == null || division.isBlank()) {
            return null;
        }

        return Arrays.stream(division.split(","))
                .map(String::trim)
                .toList();
    }
}

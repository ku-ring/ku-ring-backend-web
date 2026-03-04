package com.kustacks.kuring.club.application.port.in.dto;

import java.util.List;

public record ClubListResult(
        List<ClubItemResult> clubs
) {
}

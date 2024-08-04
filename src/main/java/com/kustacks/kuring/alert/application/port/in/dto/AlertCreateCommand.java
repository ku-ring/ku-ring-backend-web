package com.kustacks.kuring.alert.application.port.in.dto;

import java.time.LocalDateTime;

public record AlertCreateCommand(
        String title,
        String content,
        LocalDateTime alertTime
) {
}

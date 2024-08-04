package com.kustacks.kuring.alert.adapter.in.event.dto;

import java.time.LocalDateTime;

public record AlertCreateEvent(
        String title,
        String content,
        LocalDateTime alertTime
) {
}

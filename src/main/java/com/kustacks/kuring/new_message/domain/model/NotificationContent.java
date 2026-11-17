package com.kustacks.kuring.new_message.domain.model;

import lombok.Builder;

@Builder
public record NotificationContent(
        String title,
        String body
) {

    public static NotificationContent of(String title, String body) {
        return NotificationContent.builder()
                .title(title)
                .body(body)
                .build();
    }

}

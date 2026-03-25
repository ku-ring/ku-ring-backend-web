package com.kustacks.kuring.message.application.port.in.dto;

import com.kustacks.kuring.message.domain.MessageType;

public record AdminNotificationCommand(
        String type,
        String title,
        String body,
        String url
) {
    public AdminNotificationCommand(String title, String body, String url) {
        this(MessageType.ADMIN.getValue(), title, body, url);
    }
}

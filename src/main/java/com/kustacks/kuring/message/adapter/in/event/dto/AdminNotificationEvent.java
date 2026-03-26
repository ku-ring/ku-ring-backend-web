package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.AdminNotificationCommand;
import com.kustacks.kuring.message.domain.MessageType;

public record AdminNotificationEvent(
        String type,
        String title,
        String body,
        String url
) {
    public AdminNotificationEvent(String title, String body, String url) {
        this(MessageType.ADMIN.getValue(), title, body, url);
    }

    public AdminNotificationCommand toCommand() {
        return new AdminNotificationCommand(title, body, url);
    }
}

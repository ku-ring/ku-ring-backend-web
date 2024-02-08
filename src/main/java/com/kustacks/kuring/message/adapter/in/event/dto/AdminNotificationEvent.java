package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.AdminNotificationCommand;

public record AdminNotificationEvent(
        String type,
        String title,
        String body,
        String url
) {
    public AdminNotificationEvent(String title, String body, String url) {
        this("admin", title, body, url);
    }

    public AdminNotificationCommand toCommand() {
        return new AdminNotificationCommand(title, body, url);
    }
}

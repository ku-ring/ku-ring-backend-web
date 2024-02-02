package com.kustacks.kuring.message.application.port.in.dto;

public record AdminNotificationCommand(
        String type,
        String title,
        String body,
        String url
) {
    public AdminNotificationCommand(String title, String body, String url) {
        this("admin", title, body, url);
    }
}

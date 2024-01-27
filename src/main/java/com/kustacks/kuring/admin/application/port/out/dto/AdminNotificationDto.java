package com.kustacks.kuring.admin.application.port.out.dto;

public record AdminNotificationDto(
        String type,
        String title,
        String body,
        String url
) {
    public AdminNotificationDto(String title, String body, String url) {
        this("admin", title, body, url);
    }
}

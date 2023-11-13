package com.kustacks.kuring.admin.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminNotificationDto {

    private String type;
    private String title;
    private String body;
    private String url;

    public AdminNotificationDto(String title, String body, String url) {
        this.type = "admin";
        this.title = title;
        this.body = body;
        this.url = url;
    }

    public static AdminNotificationDto from(RealNotificationRequest request) {
        return new AdminNotificationDto(request.getTitle(), request.getBody(), request.getUrl());
    }
}

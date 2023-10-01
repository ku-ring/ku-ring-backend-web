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

    public AdminNotificationDto(String title, String body) {
        this.type = "admin";
        this.title = title;
        this.body = body;
    }
}

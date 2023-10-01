package com.kustacks.kuring.admin.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RealNotificationRequest {

    private String title;
    private String body;
    private String adminPassword;
}

package com.kustacks.kuring.admin.common.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RealNotificationRequest {

    private String title;
    private String body;
    private String url;
    private String adminPassword;
}

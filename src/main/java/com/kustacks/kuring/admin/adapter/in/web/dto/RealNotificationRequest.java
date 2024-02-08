package com.kustacks.kuring.admin.adapter.in.web.dto;

import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.auth.context.Authentication;

public record RealNotificationRequest(
        String title,
        String body,
        String url,
        String adminPassword
) {
    public RealNotificationCommand toCommandWithAuthentication(Authentication authentication) {
        return new RealNotificationCommand(title, body, url, adminPassword, authentication);
    }
}

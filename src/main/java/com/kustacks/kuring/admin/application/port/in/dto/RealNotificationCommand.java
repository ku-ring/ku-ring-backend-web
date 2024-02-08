package com.kustacks.kuring.admin.application.port.in.dto;

import com.kustacks.kuring.auth.context.Authentication;

public record RealNotificationCommand(
        String title,
        String body,
        String url,
        String adminPassword,
        Authentication authentication
) {
    public String getStringPrincipal() {
        return String.valueOf(this.authentication.getPrincipal());
    }
}

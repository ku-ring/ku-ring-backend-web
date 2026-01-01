package com.kustacks.kuring.admin.adapter.in.web.dto;

import com.kustacks.kuring.message.application.port.in.dto.AcademicTestNotificationCommand;

public record AcademicTestNotificationRequest(
        String title,
        String body
) {
    public AcademicTestNotificationCommand toCommand() {
        return new AcademicTestNotificationCommand(title, body);
    }
}
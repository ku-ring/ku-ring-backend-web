package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.AcademicTestNotificationCommand;

public record AcademicTestNotificationEvent(
        String title,
        String body
) {
    public AcademicTestNotificationCommand toCommand() {
        return new AcademicTestNotificationCommand(title, body);
    }
}

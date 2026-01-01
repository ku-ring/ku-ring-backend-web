package com.kustacks.kuring.message.application.port.in.dto;

public record AcademicTestNotificationCommand(
        String title,
        String body
) {
}

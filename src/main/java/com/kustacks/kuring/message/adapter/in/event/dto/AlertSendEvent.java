package com.kustacks.kuring.message.adapter.in.event.dto;

import com.kustacks.kuring.message.application.port.in.dto.AdminNotificationCommand;

public record AlertSendEvent(
        String title,
        String content
) {
    public AdminNotificationCommand toCommand() {
        return new AdminNotificationCommand(title, content, "");
    }
}

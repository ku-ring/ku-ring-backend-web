package com.kustacks.kuring.admin.adapter.in.web.dto;

import com.kustacks.kuring.admin.application.port.in.dto.TestNotificationCommand;

public record TestNotificationRequest(
        String category,
        String subject,
        String articleId
) {

    public TestNotificationCommand toCommand() {
        return new TestNotificationCommand(category, subject, articleId);
    }
}

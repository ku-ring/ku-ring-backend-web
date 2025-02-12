package com.kustacks.kuring.email.adapter.in.event.dto;

import com.kustacks.kuring.email.application.port.in.dto.EmailSendCommand;

public record EmailSendEvent(
        String from,
        String to,
        String subject,
        String text
) {
    public EmailSendCommand toCommand() {
        return new EmailSendCommand(from, to, subject, text);
    }
}

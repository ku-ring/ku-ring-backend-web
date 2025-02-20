package com.kustacks.kuring.email.application.port.in.dto;

public record EmailSendCommand(
        String from,
        String to,
        String subject,
        String text
){
}

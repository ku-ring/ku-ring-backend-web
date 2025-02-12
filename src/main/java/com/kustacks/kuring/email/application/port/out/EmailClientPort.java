package com.kustacks.kuring.email.application.port.out;

public interface EmailClientPort {
    void send(String fromEmail, String toEmail, String subject, String text);
}

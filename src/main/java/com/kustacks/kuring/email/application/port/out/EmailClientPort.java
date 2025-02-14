package com.kustacks.kuring.email.application.port.out;

import jakarta.mail.internet.MimeMessage;

public interface EmailClientPort {
    void sendEmailAsync(MimeMessage mimeMessage);
}

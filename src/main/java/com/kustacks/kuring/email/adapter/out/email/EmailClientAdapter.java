package com.kustacks.kuring.email.adapter.out.email;

import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailClientAdapter implements EmailClientPort {
    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendEmailAsync(MimeMessage message) {
        mailSender.send(message);
    }
}

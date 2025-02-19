package com.kustacks.kuring.email.adapter.out.email;

import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("prod | dev")
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

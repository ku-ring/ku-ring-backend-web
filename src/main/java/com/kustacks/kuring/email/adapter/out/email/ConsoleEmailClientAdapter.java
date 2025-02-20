package com.kustacks.kuring.email.adapter.out.email;

import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local | test")
@Component
@RequiredArgsConstructor
public class ConsoleEmailClientAdapter implements EmailClientPort {
    @Async
    @Override
    public void sendEmailAsync(MimeMessage message) {
        log.info("[ConsoleEmailClientAdapter] sendEmailAsync {}", message);
    }
}

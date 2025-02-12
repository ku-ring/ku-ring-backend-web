package com.kustacks.kuring.email.adapter.out.email;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailClientAdapter implements EmailClientPort {
    private final JavaMailSender mailSender;

    @Override
    public void send(String fromEmail, String toEmail, String subject, String text) {
        MimeMessage message = createMimeMessage(fromEmail,toEmail,subject,text);
        mailSender.send(message);
    }
    private MimeMessage createMimeMessage(String fromEmail, String toEmail, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR);
        }
        return message;
    }
}

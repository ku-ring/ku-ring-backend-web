package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.port.in.EmailCommandUseCase;
import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import com.kustacks.kuring.email.application.port.out.TemplateEnginePort;
import com.kustacks.kuring.email.application.port.out.VerificationCodeCommandPort;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class EmailCommandService implements EmailCommandUseCase {
    private final TemplateEnginePort templateEnginePort;
    private final EmailClientPort emailClientPort;
    private final VerificationCodeCommandPort verificationCodeCommandPort;

    private static final String TEMPLATE_FILE = "mail/email-verification";
    private static final String FROM_EMAIL = "no-reply@ku-ring.com";
    private static final String TO_EMIL_SUFFIX = "@konkuk.ac.kr";
    private static final String VERIFICATION_CODE_SUBJECT = "[쿠링] 이메일 인증 코드 발송";

    @Override
    public void sendVerificationEmail(String email) {
        if (!email.endsWith(TO_EMIL_SUFFIX)) {
            throw new EmailBusinessException(ErrorCode.EMAIL_INVALID_SUFFIX);
        }
        String code = createVerificationCode();
        Map<String, Object> variables = createVariables(code);
        String htmlTextWithCode = templateEnginePort.process(TEMPLATE_FILE, variables);
        MimeMessage mimeMessage = createMimeMessage(FROM_EMAIL, email, VERIFICATION_CODE_SUBJECT, htmlTextWithCode);
        emailClientPort.sendEmailAsync(mimeMessage);
        verificationCodeCommandPort.saveVerificationCode(email, code);
    }

    private MimeMessage createMimeMessage(String from, String to, String subject, String text) {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(new MimeMessage(Session.getInstance(System.getProperties())),"utf-8");
        try {
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
        } catch (MessagingException e) {
            throw new EmailBusinessException(ErrorCode.EMAIL_INVALID_TEMPLATE);
        }
        return mimeMessageHelper.getMimeMessage();
    }

    private Map<String, Object> createVariables(String code) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("code", code);
        return variables;
    }

    private String createVerificationCode() {
        final int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new EmailBusinessException(ErrorCode.EMAIL_NO_SUCH_ALGORITHM);
        }
    }
}

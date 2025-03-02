package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.port.in.EmailCommandUseCase;
import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import com.kustacks.kuring.email.application.port.out.TemplateEnginePort;
import com.kustacks.kuring.email.application.port.out.VerificationCodeCommandPort;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import com.kustacks.kuring.email.domain.VerificationCode;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
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
    private final UserQueryPort userQueryPort;

    private static final String TEMPLATE_FILE = "mail/email-verification";
    private static final String FROM_EMAIL = "no-reply@ku-ring.com";
    private static final String TO_EMIL_SUFFIX = "@konkuk.ac.kr";
    private static final String VERIFICATION_CODE_SUBJECT = "[쿠링] 이메일 인증 코드 발송";

    @Override
    public void sendVerificationEmail(String email) {
        checkDuplicateEmail(email);
        if (!email.endsWith(TO_EMIL_SUFFIX)) {
            throw new EmailBusinessException(ErrorCode.EMAIL_INVALID_SUFFIX);
        }
        String code = createVerificationCode();

        String htmlTextWithCode = templateEnginePort.process(TEMPLATE_FILE, createVariables(code));
        MimeMessage mimeMessage = createMimeMessage(FROM_EMAIL, email, VERIFICATION_CODE_SUBJECT, htmlTextWithCode);

        verificationCodeCommandPort.saveVerificationCode(new VerificationCode(email, code));

        emailClientPort.sendEmailAsync(mimeMessage);
    }

    private void checkDuplicateEmail(String email) {
        if (userQueryPort.existByEmail(email)) {
            throw new InvalidStateException(ErrorCode.EMAIL_DUPLICATE);
        }
    }

    private MimeMessage createMimeMessage(String from, String to, String subject, String text) {
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(new MimeMessage(Session.getInstance(System.getProperties())),"utf-8");
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            return mimeMessageHelper.getMimeMessage();
        } catch (MessagingException e) {
            throw new EmailBusinessException(ErrorCode.EMAIL_INVALID_TEMPLATE);
        }
    }

    private Map<String, Object> createVariables(String code) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("code", code);
        return variables;
    }

    private String createVerificationCode() {
        final int codeLength = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < codeLength; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new EmailBusinessException(ErrorCode.EMAIL_NO_SUCH_ALGORITHM);
        }
    }
}

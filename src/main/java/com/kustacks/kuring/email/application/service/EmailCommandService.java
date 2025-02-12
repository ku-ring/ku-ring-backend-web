package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.port.in.EmailCommandUseCase;
import com.kustacks.kuring.email.application.port.in.dto.EmailSendCommand;
import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import com.kustacks.kuring.email.application.port.out.EmailEventPort;
import com.kustacks.kuring.email.application.port.out.VerificationCodeCommandPort;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class EmailCommandService implements EmailCommandUseCase {
    private final EmailEventPort emailEventPort;
    private final TemplateEngine templateEngine;
    private final EmailClientPort emailClientPort;
    private final VerificationCodeCommandPort verificationCodeCommandPort;

    @Transactional
    @Override
    public void sendVerificationEmail(String email) {
        final String FROM_EMAIL = "no-reply@ku-ring.com";
        final String TO_EMIL_SUFFIX = "@konkuk.ac.kr";
        final String VERIFICATION_CODE_SUBJECT = "[쿠링] 이메일 인증 코드 발송";
        if (!email.endsWith(TO_EMIL_SUFFIX)) {
            throw new EmailBusinessException(ErrorCode.EMAIL_INVALID_SUFFIX);
        }
        String code = createCode();
        verificationCodeCommandPort.saveCode(email, code);
        String htmlTextWithCode = createHtmlTextWithCode(code);
        emailEventPort.sendEmailEvent(FROM_EMAIL, email, VERIFICATION_CODE_SUBJECT, htmlTextWithCode);
    }

    @Override
    public void sendEmail(EmailSendCommand command) {
        emailClientPort.send(command.from(), command.to(), command.subject(), command.text());
    }

    private String createHtmlTextWithCode(String code) {
        final String TEMPLATE_FILE = "mail/email-verification";
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(TEMPLATE_FILE, context);
    }

    private String createCode() {
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

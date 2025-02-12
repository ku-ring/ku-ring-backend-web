package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.port.in.EmailQueryUseCase;
import com.kustacks.kuring.email.application.port.out.VerificationCodeQueryPort;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@UseCase
@RequiredArgsConstructor
public class EmailQueryService implements EmailQueryUseCase {
    private final VerificationCodeQueryPort verificationCodeQueryPort;

    @Override
    @Transactional(readOnly = true)
    public void verifyCode(String email, String code) {
        String savedCode = verificationCodeQueryPort.findCodeByEmail(email);
        if (!isValidCode(savedCode, code)) {
            throw new EmailBusinessException(ErrorCode.EMAIL_INVALID_CODE);
        }
    }

    private boolean isValidCode(String savedCode, String code) {
        return Objects.nonNull(savedCode) &&
                savedCode.equals(code);
    }
}

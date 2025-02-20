package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.port.in.EmailVerifyUseCase;
import com.kustacks.kuring.email.application.port.out.VerificationCodeQueryPort;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import com.kustacks.kuring.email.domain.VerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class EmailVerifyService implements EmailVerifyUseCase {
    private final VerificationCodeQueryPort verificationCodeQueryPort;

    @Override
    @Transactional(readOnly = true)
    public void verifyLatestCode(String email, String code) {
        Optional<VerificationCode> latestVerificationCode = getLatestVerificationCode(email);
        latestVerificationCode.filter(verificationCode -> verificationCode.isValidCode(code))
                .orElseThrow(() -> new EmailBusinessException(ErrorCode.EMAIL_INVALID_CODE));
    }

    private Optional<VerificationCode> getLatestVerificationCode(String email) {
        List<VerificationCode> codes = verificationCodeQueryPort.findCodesByEmail(email);
        return codes.stream()
                .max(Comparator.comparing(VerificationCode::getCreatedAt));
    }
}

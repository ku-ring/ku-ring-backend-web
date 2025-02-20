package com.kustacks.kuring.email.application.port.out;

import com.kustacks.kuring.email.domain.VerificationCode;

public interface VerificationCodeCommandPort {
    void saveVerificationCode(VerificationCode verificationCode);
}

package com.kustacks.kuring.common.utils.generator;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;

public class VerificationCodeGenerator {
    private static final int CODE_LENGTH = 6;

    public static String generateVerificationCode() {
        try {
            return RandomGenerator.generateRandomNumber(CODE_LENGTH);
        } catch (RuntimeException e) {
            throw new EmailBusinessException(ErrorCode.EMAIL_NO_SUCH_ALGORITHM);
        }
    }
}

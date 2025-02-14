package com.kustacks.kuring.email.application.port.out;

public interface VerificationCodeCommandPort {
    String saveVerificationCode(String email, String verificationCode);
}

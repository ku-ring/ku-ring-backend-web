package com.kustacks.kuring.email.application.port.out;

public interface VerificationCodeCommandPort {
    String saveCode(String email, String verificationCode);
}

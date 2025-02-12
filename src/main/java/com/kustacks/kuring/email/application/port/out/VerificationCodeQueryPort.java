package com.kustacks.kuring.email.application.port.out;

public interface VerificationCodeQueryPort {
    String findCodeByEmail(String email);
}

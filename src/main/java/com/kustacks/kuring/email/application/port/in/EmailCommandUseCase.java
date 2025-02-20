package com.kustacks.kuring.email.application.port.in;

public interface EmailCommandUseCase {
    void sendVerificationEmail(String email);
}

package com.kustacks.kuring.email.application.port.in;

public interface EmailCommandUseCase {
    void sendSignupVerificationEmail(String email);

    void sendPasswordResetVerificationEmail(String email);
}

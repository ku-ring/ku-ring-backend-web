package com.kustacks.kuring.email.application.port.in;

public interface EmailVerifyUseCase {

    void verifyCode(String email, String code);
}

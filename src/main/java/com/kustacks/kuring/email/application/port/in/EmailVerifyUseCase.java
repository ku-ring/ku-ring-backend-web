package com.kustacks.kuring.email.application.port.in;

public interface EmailVerifyUseCase {

    void verifyLatestCode(String email, String code);
}

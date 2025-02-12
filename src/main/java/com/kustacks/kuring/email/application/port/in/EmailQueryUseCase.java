package com.kustacks.kuring.email.application.port.in;

public interface EmailQueryUseCase {

    void verifyCode(String email, String code);
}

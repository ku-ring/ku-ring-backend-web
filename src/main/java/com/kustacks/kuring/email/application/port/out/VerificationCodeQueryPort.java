package com.kustacks.kuring.email.application.port.out;

import com.kustacks.kuring.email.domain.VerificationCode;

import java.util.List;

public interface VerificationCodeQueryPort {
    List<VerificationCode> findCodesByEmail(String email);
}

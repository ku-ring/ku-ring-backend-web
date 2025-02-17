package com.kustacks.kuring.email.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.email.application.port.out.VerificationCodeCommandPort;
import com.kustacks.kuring.email.application.port.out.VerificationCodeQueryPort;
import com.kustacks.kuring.email.domain.VerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("local | test")
@PersistenceAdapter
@RequiredArgsConstructor
public class ConsoleVerificationCodePersistenceAdapter implements VerificationCodeCommandPort, VerificationCodeQueryPort {

    @Override
    public void saveVerificationCode(VerificationCode verificationCode) {
        log.info("[ConsoleVerificationCodePersistenceAdapter] saveVerificationCode {}", verificationCode.getCode());

    }

    @Override
    public List<VerificationCode> findCodesByEmail(String email) {
        if (email.equals("client@konkuk.ac.kr")) {
            return List.of(new VerificationCode(email,"123456"));
        }
        return new ArrayList<>();
    }
}

package com.kustacks.kuring.email.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.email.application.port.out.VerificationCodeCommandPort;
import com.kustacks.kuring.email.application.port.out.VerificationCodeQueryPort;
import com.kustacks.kuring.email.domain.VerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("prod | dev")
@PersistenceAdapter
@RequiredArgsConstructor
public class VerificationCodePersistenceAdapter implements VerificationCodeCommandPort, VerificationCodeQueryPort {

    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    public void saveVerificationCode(VerificationCode verificationCode) {
        verificationCodeRepository.save(verificationCode);
    }

    @Override
    public List<VerificationCode> findCodesByEmail(String email) {
        return verificationCodeRepository.findByEmail(email);
    }
}

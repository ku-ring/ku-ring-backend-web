package com.kustacks.kuring.email.adapter.out.persistence;

import com.kustacks.kuring.email.domain.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    List<VerificationCode> findByEmail(String email);
}

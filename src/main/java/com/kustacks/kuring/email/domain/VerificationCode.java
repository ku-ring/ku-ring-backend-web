package com.kustacks.kuring.email.domain;

import com.kustacks.kuring.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "verification_code")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationCode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 6, nullable = false)
    private String code;

    public VerificationCode(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public boolean isValidCode(String code) {
        return this.code.equals(code);
    }
}

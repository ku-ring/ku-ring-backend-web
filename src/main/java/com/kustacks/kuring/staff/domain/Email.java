package com.kustacks.kuring.staff.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    // RFC 5322 Official Standard
    private static final String REGEX_EMAIL = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final Pattern compiledEmailPattern = Pattern.compile(REGEX_EMAIL);

    @Column(name = "email", length = 40, nullable = false)
    private String value;

    public Email(String email) {
        if (!this.isValidEmail(email)) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

        this.value = email;
    }

    private boolean isValidEmail(String email) {
        return !Objects.isNull(email) && patternMatches(email);
    }

    private boolean patternMatches(String email) {
        return compiledEmailPattern
                .matcher(email)
                .matches();
    }

    public boolean isSameValue(String email) {
        return this.value.equals(email);
    }
}

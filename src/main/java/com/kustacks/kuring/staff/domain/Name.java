package com.kustacks.kuring.staff.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {

    @Column(name = "name", length = 64, nullable = false)
    private String value;

    public Name(String name) {
        if(isNotValidName(name)) {
            throw new IllegalArgumentException("이름은 필수 입력값입니다.");
        }

        this.value = name;
    }

    public String getValue() {
        return value;
    }

    private static boolean isNotValidName(String name) {
        return Objects.isNull(name) || name.isBlank();
    }

    public boolean isSameValue(String name) {
        return this.value.equals(name);
    }
}

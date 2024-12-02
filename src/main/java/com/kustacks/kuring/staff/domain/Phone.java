package com.kustacks.kuring.staff.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Phone {

    private static final Pattern phoneNumberPatternOne
            = Pattern.compile("^\\d{2,3}-\\d{3,4}-\\d{4}$");
    private static final Pattern phoneNumberPatternTwo
            = Pattern.compile("(\\d{2})[)\\s]*(\\d{3,4})[-\\s]*(\\d{4})");
    private static final Pattern phoneNumberPatternThree
            = Pattern.compile("(\\d{3,4})[-\\s]*(\\d{4})");
    private static final String SEOUL_AREA_CODE = "02";
    private static final String DELIMITER = "-";
    private static final String EMPTY_NUMBER = "";

    @Column(name = "phone", length = 64)
    private String value;

    public Phone(String phone) {
        if(isEmptyNumbers(phone)) {
            this.value = EMPTY_NUMBER;
            return;
        }

        if (isValidNumbersAndSet(phone)) {
            return;
        }

        throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다.");
    }

    public String getValue() {
        return value;
    }

    private boolean isValidNumbersAndSet(String phone) {
        phone = phone.trim();
        if(phoneNumberPatternOne.matcher(phone).matches()) {
            this.value = phone;
            return true;
        }

        Matcher matcher = phoneNumberPatternTwo.matcher(phone);
        if(matcher.matches()) {
            this.value = matcher.group(1) + DELIMITER + matcher.group(2) + DELIMITER + matcher.group(3);
            return true;
        }

        matcher = phoneNumberPatternThree.matcher(phone);
        if(matcher.matches()) {
            this.value = SEOUL_AREA_CODE + DELIMITER + matcher.group(1) + DELIMITER + matcher.group(2);
            return true;
        }

        if(phone.length() >= 12) {
            this.value = phone;
            return true;
        }

        return false;
    }

    private static boolean isEmptyNumbers(String phone) {
        return phone == null || phone.isBlank();
    }

    public boolean isSameValue(String phone) {
        return this.value.equals(phone);
    }
}

package com.kustacks.kuring.common.utils.converter;

import java.util.regex.Pattern;

public class PhoneNumberConverter {

    private static final Pattern LAST_FOUR_NUMBER_PATTERN = Pattern.compile("\\d{4}");
    private static final Pattern FULL_NUMBER_PATTERN = Pattern.compile("02-\\d{3,4}-\\d{4}");
    private static final Pattern FULL_NUMBER_WITH_PARENTHESES_PATTERN = Pattern.compile("02[)]\\d{3,4}-\\d{4}");

    public static String convertFullExtensionNumber(String number) {
        if (number == null || number.isBlank()) {
            return "";
        }
        if (LAST_FOUR_NUMBER_PATTERN.matcher(number).matches()) {
            return "02-450-" + number;
        }
        if (FULL_NUMBER_PATTERN.matcher(number).matches()) {
            return number;
        }
        if (FULL_NUMBER_WITH_PARENTHESES_PATTERN.matcher(number).matches()) {
            return number.replace(")", "-");
        }

        //기타 이상한 형식은 빈공백으로 저장 ex. 218) 이게뭔데;
        return "";
    }
}

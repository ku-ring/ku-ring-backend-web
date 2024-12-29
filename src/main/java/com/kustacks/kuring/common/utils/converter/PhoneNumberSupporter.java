package com.kustacks.kuring.common.utils.converter;

import java.util.regex.Pattern;

public class PhoneNumberSupporter {

    private static final Pattern LAST_FOUR_NUMBER_PATTERN = Pattern.compile("\\d{4}");
    private static final Pattern FULL_NUMBER_PATTERN = Pattern.compile("02-\\d{3,4}-\\d{4}");
    private static final Pattern FULL_NUMBER_WITH_PARENTHESES_PATTERN = Pattern.compile("02[)]\\d{3,4}-\\d{4}");

    private static final String EMPTY_PHONE = "";

    public static boolean isNullOrBlank(String number) {
        return number == null || number.isBlank();
    }

    public static String convertFullExtensionNumber(String number) {
        if (isNullOrBlank(number)) {
            return EMPTY_PHONE;
        }

        if (FULL_NUMBER_PATTERN.matcher(number).matches()) {
            return number;
        }
        if (containsLastFourNumber(number)) {
            return "02-450-" + number;
        }
        if (containsParenthesesPattern(number)) {
            return number.replace(")", "-");
        }

        return EMPTY_PHONE;
    }

    private static boolean containsLastFourNumber(String number) {
        return LAST_FOUR_NUMBER_PATTERN.matcher(number).matches();
    }

    private static boolean containsParenthesesPattern(String number) {
        return FULL_NUMBER_WITH_PARENTHESES_PATTERN.matcher(number).matches();
    }
}

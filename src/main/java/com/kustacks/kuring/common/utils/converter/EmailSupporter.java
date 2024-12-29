package com.kustacks.kuring.common.utils.converter;

import java.util.Arrays;
import java.util.regex.Pattern;

public class EmailSupporter {
    private static final Pattern AT_PATTERN = Pattern.compile("\\s+at\\s+");
    private static final Pattern DOT_PATTERN = Pattern.compile("\\s+dot\\s+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+$");

    private static final String KONKUK_DOMAIN = "@konkuk.ac.kr";
    private static final String EMPTY_EMAIL = "";

    public static boolean isNullOrBlank(String email) {
        return email == null || email.isBlank();
    }

    public static String convertValidEmail(String email) {
        if (isNullOrBlank(email)) {
            return EMPTY_EMAIL;
        }

        String[] emailGroups = splitEmails(email);
        String[] normalizedEmails = normalizeEmails(emailGroups);

        //여러 이메일 중 konkuk을 우선 선택, 없으면 첫번째 내용
        return selectPreferredEmail(normalizedEmails);
    }

    private static String[] splitEmails(String email) {
        return email.split("[/,]");
    }

    private static String[] normalizeEmails(String[] emailGroups) {
        return Arrays.stream(emailGroups)
                .map(EmailSupporter::normalizeEmail)
                .toArray(String[]::new);
    }

    private static String normalizeEmail(String email) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            return email;
        }

        if (containsSubstitutePatterns(email)) {
            return replaceSubstitutePatterns(email);
        }

        return EMPTY_EMAIL;
    }

    private static String replaceSubstitutePatterns(String email) {
        return email.replaceAll(DOT_PATTERN.pattern(), ".")
                .replaceAll(AT_PATTERN.pattern(), "@");
    }

    private static boolean containsSubstitutePatterns(String email) {
        return DOT_PATTERN.matcher(email).find() && AT_PATTERN.matcher(email).find();
    }

    // Konkuk 도메인 우선 선택
    private static String selectPreferredEmail(String[] emails) {
        return Arrays.stream(emails)
                .filter(email -> email.endsWith(KONKUK_DOMAIN))
                .findFirst()
                .orElseGet(() -> emails.length > 0 ? emails[0] : EMPTY_EMAIL);
    }
}

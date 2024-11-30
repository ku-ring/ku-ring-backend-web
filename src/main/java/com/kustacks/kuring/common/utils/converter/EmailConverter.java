package com.kustacks.kuring.common.utils.converter;

import java.util.regex.Pattern;

public class EmailConverter {
    private static final Pattern AT_PATTERN = Pattern.compile("\\s+at\\s+");
    private static final Pattern DOT_PATTERN = Pattern.compile("\\s+dot\\s+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+$");

    private static final String KONKUK_DOMAIN = "@konkuk.ac.kr";

    public static String convertValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return ""; // 빈 입력 처리
        }

        //여러 이메일인 경우 있으니 분리.
        String[] emailGroups = email.split("[/,]");
        //정상 구조가 아닌 경우 구조 정상화
        for (int i = 0; i < emailGroups.length; i++) {
            emailGroups[i] = normalizeEmail(emailGroups[i]);
        }

        //여러 이메일 중 konkuk을 우선 선택, 없으면 첫번째 내용
        return selectEmail(emailGroups);
    }

    private static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }

        // 정상 이메일인지 확인
        if (EMAIL_PATTERN.matcher(email).matches()) {
            return email;
        }

        // "@", "." 대신 "at", "dot"으로 되어있는 경우 변환
        if (DOT_PATTERN.matcher(email).find() && AT_PATTERN.matcher(email).find()) {
            return email.replaceAll(DOT_PATTERN.pattern(), ".")
                    .replaceAll(AT_PATTERN.pattern(), "@");
        }

        // 기타 이상한 형식은 빈공백으로 저장
        return "";
    }

    // Konkuk 도메인 우선 선택
    private static String selectEmail(String[] emails) {
        for (String email : emails) {
            if (email.endsWith(KONKUK_DOMAIN)) {
                return email;
            }
        }
        return emails[0];
    }
}

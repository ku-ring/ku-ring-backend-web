package com.kustacks.kuring.worker.update.calendar;

import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AcademicEventSummaryNormalizer {

    // 괄호 안에 시간/날짜 정보가 있는 패턴
    // 예: (2. 25. 화 9:30 ~ 2. 26 수 17:00), (9:30 ~)
    // 1단계: 모든 괄호 찾기
    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\([^)]+\\)");
    // 2단계: 괄호 내용에서 시간/날짜 확인
    private static final Pattern TIME_IN_BRACKET = Pattern.compile("\\d+[:.]|\\d+\\s*[월화수목금토일]");

    // 괄호 없는 날짜/시간 패턴 제거 (ReDoS 방지)
    // 예: 14.(월) 10:30 ~ 15.(화) 16:30, ~ 5.(월) 16:30
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("\\s*~?\\s*\\d+\\.\\s*\\([월화수목금토일]\\)\\s*\\d+[:.]\\d+[^\\r\\n]*$");

    // 단순 시간 패턴 제거 (HH:MM 형식)
    // 예: 17:00
    private static final Pattern SIMPLE_TIME_PATTERN = Pattern.compile("\\s+\\d{1,2}:\\d{2}\\s*$");

    // ※ 기호 이후의 모든 텍스트 제거 패턴 (줄바꿈 전까지)
    private static final Pattern REFERENCE_MARK_PATTERN = Pattern.compile("※[^\\r\\n]*$");

    public static String normalize(String summary) {
        if (summary == null || summary.isBlank()) {
            return null;
        }

        String processed = summary.trim();

        // 1. 괄호 안 날짜/시간 정보 제거
        processed = removeDateTimeBrackets(processed);

        // 2. 괄호 없는 직접 날짜/시간 정보 제거
        processed = removeDateTime(processed);

        // 3. 단순 시간 정보 제거 (HH:MM)
        processed = removeSimpleTime(processed);

        // 4. ※ 기호 이후 모든 텍스트 제거
        processed = removeTextAfterSymbol(processed);

        // 최종 trim
        processed = processed.trim();

        return processed.isEmpty() ? null : processed;
    }

    /**
     * 괄호 안의 날짜/시간 정보 제거 (차수 정보는 유지)
     * 예: "전체학년 수강신청 (2. 25. 화 9:30 ~ 2. 26 수 17:00)" → "전체학년 수강신청"
     * 예: "강의시간표조회 (9:30 ~)" → "강의시간표조회"
     * 예: "취득학점포기(3차)" → "취득학점포기(3차)" (차수 정보 유지)
     */
    private static String removeDateTimeBrackets(String text) {
        return BRACKET_PATTERN.matcher(text).replaceAll(matchResult -> {
            String bracketContent = matchResult.group();
            String innerContent = bracketContent.substring(1, bracketContent.length() - 1);

            // 괄호 안에 시간/날짜 정보가 있으면 전체 괄호를 제거
            if (TIME_IN_BRACKET.matcher(innerContent).find()) {
                return ""; // 괄호와 내용 모두 제거
            }

            return bracketContent; // 시간/날짜 정보가 없으면 괄호 유지 (차수 정보 등)
        }).trim();
    }

    /**
     * 괄호 없는 직접 날짜/시간 정보 제거 (~ 기호 포함)
     * 예: "취득학점포기(3차) 14.(월) 10:30 ~ 15.(화) 16:30" → "취득학점포기(3차)"
     * 예: "최정성적이의신청 ~ 5.(월) 16:30" → "최정성적이의신청"
     */
    private static String removeDateTime(String text) {
        return DATE_TIME_PATTERN.matcher(text).replaceAll("").trim();
    }

    /**
     * 단순 시간 정보 제거 (HH:MM 형식)
     * 예: "복학신청 마감 17:00" → "복학신청 마감"
     */
    private static String removeSimpleTime(String text) {
        return SIMPLE_TIME_PATTERN.matcher(text).replaceAll("").trim();
    }

    /**
     * ※ 기호 이후의 모든 텍스트 제거
     * 예: "취득학점포기(3차)※주말(토,일)미포함" → "취득학점포기(3차)"
     */
    private static String removeTextAfterSymbol(String text) {
        return REFERENCE_MARK_PATTERN.matcher(text).replaceAll("").trim();
    }
}
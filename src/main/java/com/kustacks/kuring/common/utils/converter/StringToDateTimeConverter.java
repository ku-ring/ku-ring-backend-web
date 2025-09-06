package com.kustacks.kuring.common.utils.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringToDateTimeConverter {

    private static final String REGEX_DATE_TIME = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final Pattern compiledDateTimePattern = Pattern.compile(REGEX_DATE_TIME);

    private static final String REGEX_DATE_TIME_T = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,6})?$";
    private static final Pattern compiledDateTimeTPattern = Pattern.compile(REGEX_DATE_TIME_T);

    private static final String REGEX_LOCAL = "^\\d{8}T\\d{6}$";
    private static final Pattern compiledLocalDateTimePattern = Pattern.compile(REGEX_LOCAL);

    private static final String REGEX_DATE = "^\\d{8}$";
    private static final Pattern compiledDatePattern = Pattern.compile(REGEX_DATE);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.KOREA);
    private static final DateTimeFormatter dateTimeTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").withLocale(Locale.KOREA);
    private static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final int MAX_DATE_TIME_LENGTH = 26;

    public static LocalDateTime convert(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalArgumentException("Invalid date time format: " + dateTime);
        }

        if (dateTime.length() > MAX_DATE_TIME_LENGTH) {
            dateTime = dateTime.substring(0, MAX_DATE_TIME_LENGTH);
        }

        if (isDateTime(dateTime)) {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        }

        if (isDateTimeT(dateTime)) {
            LocalDateTime parsed = LocalDateTime.parse(dateTime, dateTimeTFormatter);
            return parsed.withNano(0);
        }

        if (isLocalDateTime(dateTime)) {
            return LocalDateTime.parse(dateTime, localDateTimeFormatter);
        }

        if (isDate(dateTime)) {
            return LocalDateTime.parse(dateTime + "T000000", localDateTimeFormatter);
        }

        throw new IllegalArgumentException("Invalid date time format: " + dateTime);
    }

    private static boolean isDateTime(String dateTime) {
        return compiledDateTimePattern.matcher(dateTime).matches();
    }

    private static boolean isDateTimeT(String dateTime) {
        return compiledDateTimeTPattern.matcher(dateTime).matches();
    }

    private static boolean isLocalDateTime(String dateTime) {
        return compiledLocalDateTimePattern.matcher(dateTime).matches();
    }

    private static boolean isDate(String dateTime) {
        return compiledDatePattern.matcher(dateTime).matches();
    }
}

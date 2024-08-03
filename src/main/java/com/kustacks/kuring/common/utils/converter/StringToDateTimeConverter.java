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

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.KOREA);
    private static final DateTimeFormatter dateTimeTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").withLocale(Locale.KOREA);

    public static LocalDateTime convert(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalArgumentException("Invalid date time format: " + dateTime);
        }

        if (isDateTime(dateTime)) {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        }

        if (isDateTimeT(dateTime)) {
            LocalDateTime parsed = LocalDateTime.parse(dateTime, dateTimeTFormatter);
            return parsed.withNano(0);
        }

        throw new IllegalArgumentException("Invalid date time format: " + dateTime);
    }

    private static boolean isDateTime(String dateTime) {
        return compiledDateTimePattern.matcher(dateTime).matches();
    }

    private static boolean isDateTimeT(String dateTime) {
        return compiledDateTimeTPattern.matcher(dateTime).matches();
    }
}

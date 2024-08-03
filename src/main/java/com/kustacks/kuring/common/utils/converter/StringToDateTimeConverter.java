package com.kustacks.kuring.common.utils.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringToDateTimeConverter {

    private static final String REGEX_DATE_TIME = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final Pattern compiledDateTimePattern = Pattern.compile(REGEX_DATE_TIME);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.KOREA);

    public static LocalDateTime convert(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new IllegalArgumentException("Invalid date time format: " + dateTime);
        }

        if (isValidDateTime(dateTime)) {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        }

        throw new IllegalArgumentException("Invalid date time format: " + dateTime);
    }

    private static boolean isValidDateTime(String dateTime) {
        return compiledDateTimePattern.matcher(dateTime).matches();
    }
}

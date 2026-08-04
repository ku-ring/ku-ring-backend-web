package com.kustacks.kuring.common.utils;

import java.time.LocalDate;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static boolean isWeekend(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }
}

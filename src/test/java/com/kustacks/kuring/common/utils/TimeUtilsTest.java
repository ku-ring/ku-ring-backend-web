package com.kustacks.kuring.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("유틸 : TimeUtils")
class TimeUtilsTest {

    @Test
    @DisplayName("토요일과 일요일을 주말로 판단한다")
    void determine_weekend() {
        // given
        LocalDate saturday = LocalDate.of(2026, 7, 25);
        LocalDate sunday = LocalDate.of(2026, 7, 26);

        // when
        boolean saturdayResult = TimeUtils.isWeekend(saturday);
        boolean sundayResult = TimeUtils.isWeekend(sunday);

        // then
        assertAll(
                () -> assertThat(saturdayResult).isTrue(),
                () -> assertThat(sundayResult).isTrue()
        );
    }

    @Test
    @DisplayName("평일을 주말이 아닌 것으로 판단한다")
    void determine_weekday() {
        // given
        LocalDate weekday = LocalDate.of(2026, 7, 20);

        // when
        boolean result = TimeUtils.isWeekend(weekday);

        // then
        assertThat(result).isFalse();
    }
}

package com.kustacks.kuring.building.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("도메인 : OperatingHours")
class OperatingHoursTest {

    @Test
    @DisplayName("지정 운영시간은 시작과 종료 시각을 가진다")
    void create_scheduled_operating_hours() {
        // when
        OperatingHours hours = new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        );

        // then
        assertThat(hours.getOpensAt()).isEqualTo(LocalTime.of(9, 0));
        assertThat(hours.getClosesAt()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    @DisplayName("지정 운영시간에 시각이 없으면 생성할 수 없다")
    void reject_scheduled_operating_hours_without_times() {
        assertThatIllegalArgumentException().isThrownBy(() -> new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                null,
                null
        ));
    }

    @Test
    @DisplayName("24시간 운영은 시작과 종료 시각을 저장하지 않는다")
    void create_open_24_hours_without_times() {
        // when
        OperatingHours hours = new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.OPEN_24_HOURS,
                null,
                null
        );

        // then
        assertThat(hours.getStatus()).isEqualTo(OperatingHoursStatus.OPEN_24_HOURS);
        assertThat(hours.getOpensAt()).isNull();
        assertThat(hours.getClosesAt()).isNull();
    }
}

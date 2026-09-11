package com.kustacks.kuring.calendar.domain;

import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;
import com.kustacks.kuring.worker.update.calendar.AcademicEventConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

public class AcademicEventConverterTest {

    @Test
    @DisplayName("DTEND가 없는 종일 일정은 해당 날짜의 23시 59분 59초로 종료 시간을 설정한다")
    void convert_all_day_event_without_dtend() {
        // given
        IcsEvent icsEvent = IcsEvent.builder()
                .uid("test-uid")
                .summary("개강")
                .dtstart("20260831")
                .dtend(null)
                .build();

        // when
        Optional<AcademicEvent> result =
                AcademicEventConverter.convertToAcademicEvent(icsEvent);

        // then
        assertThat(result).isPresent();

        AcademicEvent event = result.get();

        assertThat(event.getStartTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 31, 0, 0, 0));

        assertThat(event.getEndTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 31, 23, 59, 59));
    }

    @Test
    @DisplayName("DTEND가 없는 일반 일정은 시작 시간과 종료 시간을 동일하게 설정한다")
    void convert_non_all_day_event_without_dtend() {
        // given
        IcsEvent icsEvent = IcsEvent.builder()
                .uid("test-uid")
                .summary("수강신청")
                .dtstart("20260818T093000")
                .dtend(null)
                .build();

        // when
        Optional<AcademicEvent> result =
                AcademicEventConverter.convertToAcademicEvent(icsEvent);

        // then
        assertThat(result).isPresent();

        AcademicEvent event = result.get();

        assertThat(event.getStartTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 18, 9, 30));

        assertThat(event.getEndTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 18, 9, 30));
    }

    @Test
    @DisplayName("DTSTART가 없으면 변환하지 않는다")
    void convert_without_dtstart() {
        // given
        IcsEvent icsEvent = IcsEvent.builder()
                .uid("test-uid")
                .summary("개강")
                .dtstart(null)
                .dtend("20260818")
                .build();

        // when
        Optional<AcademicEvent> result =
                AcademicEventConverter.convertToAcademicEvent(icsEvent);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("DTSTART와 DTEND의 형식이 다르면 변환하지 않는다")
    void convert_different_dtstart_dtend_format() {
        // given
        IcsEvent icsEvent = IcsEvent.builder()
                .uid("test-uid")
                .summary("개강")
                .dtstart("20260831")
                .dtend("20260901T090000")
                .build();

        // when
        Optional<AcademicEvent> result =
                AcademicEventConverter.convertToAcademicEvent(icsEvent);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("종일 일정은 종료 시간을 1초 앞당겨 변환한다")
    void convert_all_day_event() {
        // given
        IcsEvent icsEvent = IcsEvent.builder()
                .uid("test-uid")
                .summary("개강")
                .dtstart("20260817")
                .dtend("20260818")
                .build();

        // when
        Optional<AcademicEvent> result =
                AcademicEventConverter.convertToAcademicEvent(icsEvent);

        // then
        assertThat(result).isPresent();

        AcademicEvent event = result.get();

        assertThat(event.getStartTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 17, 0, 0, 0));

        assertThat(event.getEndTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 17, 23, 59, 59));
    }

    @Test
    @DisplayName("종일 일정이 아닌 경우 시작 시간과 종료 시간을 그대로 유지한다")
    void convert_non_all_day_event() {
        // given
        IcsEvent icsEvent = IcsEvent.builder()
                .uid("test-uid")
                .summary("수강신청")
                .dtstart("20260818T093000")
                .dtend("20260818T170000")
                .build();

        // when
        Optional<AcademicEvent> result =
                AcademicEventConverter.convertToAcademicEvent(icsEvent);

        // then
        assertThat(result).isPresent();

        AcademicEvent event = result.get();

        assertThat(event.getStartTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 18, 9, 30));

        assertThat(event.getEndTime())
                .isEqualTo(LocalDateTime.of(2026, 8, 18, 17, 0));
    }
}

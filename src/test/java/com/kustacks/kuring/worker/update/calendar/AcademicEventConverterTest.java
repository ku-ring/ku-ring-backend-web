package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


class AcademicEventConverterTest {

    private List<IcsEvent> testIcsEvents = List.of(
            new IcsEvent(
                    "040000008200E00074C5B7101A82E0080000000033C0B88EDFBAD9010000000000000000100000006ECC99F08500CA4096B671977C2912F7",
                    "하계방학",
                    "\n",
                    "20240622",
                    "20240902",
                    "PUBLIC",
                    "5",
                    "20250826T143707Z",
                    "TRANSPARENT",
                    "CONFIRMED",
                    "0",
                    "\n"
            ),
            new IcsEvent(
                    "040000008200E00074C5B7101A82E0080000000014125995CCA1DA01000000000000000010000000FA658BFA03493F4A9597C68E4BF868BF",
                    "폐강교과목 공지(1차)(9:00~)",
                    "\n",
                    "20240902",
                    "20240903",
                    "PUBLIC",
                    "5",
                    "20250826T143707Z",
                    "TRANSPARENT",
                    "CONFIRMED",
                    "0",
                    ""
            )
    );

    @DisplayName("단건 학사 이벤트 변환을 수행할 수 있다.")
    @Test
    void convert_ics_event_to_academic_event() {
        //given
        IcsEvent icsEvent = testIcsEvents.get(0);

        //when
        AcademicEvent academicEvent = AcademicEventConverter.convertToAcademicEvent(icsEvent).orElse(null);

        //then
        assertEventFields(academicEvent, "하계방학", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 6, 22, 0, 0),
                LocalDateTime.of(2024, 9, 1, 23, 59, 59));
    }

    @DisplayName("다건 학사 이벤트 변환을 수행할 수 있다.")
    @Test
    void convert_multi_ics_event_to_academic_event() {
        //when
        List<AcademicEvent> academicEvents = AcademicEventConverter.convertToAcademicEvents(testIcsEvents);

        //then
        assertEventFields(academicEvents.get(0), "하계방학", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 6, 22, 0, 0),
                LocalDateTime.of(2024, 9, 1, 23, 59, 59));

        assertEventFields(academicEvents.get(1), "폐강교과목 공지(1차)", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 9, 2, 0, 0),
                LocalDateTime.of(2024, 9, 2, 23, 59, 59));
    }

    @DisplayName("공휴일 이벤트는 변환에서 제외")
    @ParameterizedTest
    @ValueSource(strings = {
            "신정공휴일",
            "설날연휴공휴일",
            "추석연휴공휴일",
            "어린이날공휴일",
            "광복절공휴일",
            "크리스마스공휴일",
            "공휴일입니다"
    })
    void convert_exclude_holiday_events(String holidaySummary) {
        // given
        IcsEvent holidayEvent = new IcsEvent(
                "test-uid",
                holidaySummary,
                "설명",
                "20240101",
                "20240102",
                "PUBLIC",
                "0",
                "20250826T143707Z",
                "TRANSPARENT",
                "CONFIRMED",
                "0",
                ""
        );

        // when
        AcademicEvent academicEvent = AcademicEventConverter.convertToAcademicEvent(holidayEvent).orElse(null);

        // then
        assertThat(academicEvent).isNull();
    }

    @DisplayName("일반 학사일정 이벤트는 변환 대상")
    @ParameterizedTest
    @ValueSource(strings = {
            "수강신청",
            "중간고사",
            "기말고사",
            "휴학신청",
            "개강",
            "방학"
    })
    void convert_include_normal_events(String normalSummary) {
        // given
        IcsEvent normalEvent = new IcsEvent(
                "test-uid",
                normalSummary,
                "설명",
                "20240101",
                "20240102",
                "PUBLIC",
                "0",
                "20250826T143707Z",
                "TRANSPARENT",
                "CONFIRMED",
                "0",
                ""
        );

        // when
        AcademicEvent academicEvent = AcademicEventConverter.convertToAcademicEvent(normalEvent).orElse(null);

        // then
        assertThat(academicEvent).isNotNull();
        assertThat(academicEvent.getSummary()).isEqualTo(normalSummary);
    }

    @DisplayName("null이나 빈 summary는 변환에서 제외")
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void convert_exclude_empty_summary(String emptySummary) {
        // given
        IcsEvent emptyEvent = new IcsEvent(
                "test-uid",
                emptySummary,
                "설명",
                "20240101",
                "20240102",
                "PUBLIC",
                "0",
                "20250826T143707Z",
                "TRANSPARENT",
                "CONFIRMED",
                "0",
                ""
        );

        // when
        AcademicEvent academicEvent = AcademicEventConverter.convertToAcademicEvent(emptyEvent).orElse(null);

        // then
        assertThat(academicEvent).isNull();
    }

    @DisplayName("null summary는 변환에서 제외")
    @Test
    void convert_exclude_null_summary() {
        // given
        IcsEvent nullEvent = new IcsEvent(
                "test-uid",
                null,
                "설명",
                "20240101",
                "20240102",
                "PUBLIC",
                "0",
                "20250826T143707Z",
                "TRANSPARENT",
                "CONFIRMED",
                "0",
                ""
        );

        // when
        AcademicEvent academicEvent = AcademicEventConverter.convertToAcademicEvent(nullEvent).orElse(null);

        // then
        assertThat(academicEvent).isNull();
    }

    private void assertEventFields(AcademicEvent academicEvent, String summary, Integer sequence, Transparent transparent,
                                   Boolean notifyEnabled, LocalDateTime startDate, LocalDateTime endDate) {
        assertAll(
                () -> assertThat(academicEvent).isNotNull(),
                () -> assertThat(academicEvent.getSummary()).isEqualTo(summary),
                () -> assertThat(academicEvent.getSequence()).isEqualTo(sequence),
                () -> assertThat(academicEvent.getTransparent()).isEqualTo(transparent),
                () -> assertThat(academicEvent.getNotifyEnabled()).isEqualTo(notifyEnabled),
                () -> assertThat(academicEvent.getStartTime()).isEqualTo(startDate),
                () -> assertThat(academicEvent.getEndTime()).isEqualTo(endDate)
        );
    }

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

    @DisplayName("종일 이벤트의 종료 시간이 시작 시간보다 이전이면 변환에서 제외")
    @Test
    void convert_exclude_all_day_event_when_end_time_is_before_start_time() {
        // given
        IcsEvent icsEvent = new IcsEvent(
                "test-uid",
                "테스트 일정",
                "설명",
                "20260817",
                "20260817",
                "PUBLIC",
                "0",
                "20260817T000000Z",
                "TRANSPARENT",
                "CONFIRMED",
                "0",
                ""
        );

        // when
        AcademicEvent academicEvent =
                AcademicEventConverter.convertToAcademicEvent(icsEvent).orElse(null);

        // then
        assertThat(academicEvent).isNull();
    }
}
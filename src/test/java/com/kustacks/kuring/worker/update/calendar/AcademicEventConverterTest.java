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
                LocalDateTime.of(2024, 9, 2, 0, 0));
    }

    @DisplayName("다건 학사 이벤트 변환을 수행할 수 있다.")
    @Test
    void convert_multi_ics_event_to_academic_event() {
        //when
        List<AcademicEvent> academicEvents = AcademicEventConverter.convertToAcademicEvents(testIcsEvents);

        //then
        assertEventFields(academicEvents.get(0), "하계방학", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 6, 22, 0, 0),
                LocalDateTime.of(2024, 9, 2, 0, 0));

        assertEventFields(academicEvents.get(1), "폐강교과목 공지(1차)", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 9, 2, 0, 0),
                LocalDateTime.of(2024, 9, 3, 0, 0));
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
}
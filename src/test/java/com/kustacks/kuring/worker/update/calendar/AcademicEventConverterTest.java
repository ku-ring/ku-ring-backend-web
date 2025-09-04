package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        AcademicEvent academicEvent = AcademicEventConverter.convertToAcademicEvent(icsEvent);

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

        assertEventFields(academicEvents.get(1), "폐강교과목 공지(1차)(9:00~)", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 9, 2, 0, 0),
                LocalDateTime.of(2024, 9, 3, 0, 0));
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
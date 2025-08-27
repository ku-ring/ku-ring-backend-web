package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.worker.scrap.calendar.IcsScraper;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("AcademicEventUpdater 통합 테스트")
class AcademicEventUpdaterTest extends IntegrationTestSupport {

    @Autowired
    private AcademicEventUpdater academicEventUpdater;

    @Autowired
    private AcademicEventQueryPort academicEventQueryPort;

    @MockBean
    private IcsScraper icsScraper;

    private final String ORIGINAL_CALENDAR_FILE = "src/test/resources/calendar/academic-calendar-origin.ics";
    private final String UPDATED_CALENDAR_FILE = "src/test/resources/calendar/academic-calendar-updated.ics";

    private final List<String> originEventUids = List.of("040000008200E00074C5B7101A82E0080000000033C0B88EDFBAD9010000000000000000100000006ECC99F08500CA4096B671977C2912F7", // 하계방학
            "040000008200E00074C5B7101A82E0080000000014125995CCA1DA01000000000000000010000000FA658BFA03493F4A9597C68E4BF868BF"); //폐강교과목 공지

    private final List<String> updatedEventUids = List.of("040000008200E00074C5B7101A82E0080000000033C0B88EDFBAD9010000000000000000100000006ECC99F08500CA4096B671977C2912F7", // 하계방학
            "040000008200E00074C5B7101A82E0080000000014125995CCA1DA01000000000000000010000000FA658BFA03493F4A9597C68E4BF868BF", //폐강교과목 공지(수정됨)
            "140000008200E00074C5B7101A82E00800000000C1545CD275FFDB01000000000000000010000000A6974AF4EE07FD42979F2F3DA1D208DC"); //김한주 생일(새로 추가됨)

    @Test
    @DisplayName("원본 캘린더 파일로 초기 데이터를 DB에 저장한다")
    void should_save_initial_calendar_data_to_db() throws IOException, ParserException {
        // given - 원본 캘린더 파일을 스크랩하도록 Mock 설정
        Calendar originalCalendar = loadCalendarFromFile(ORIGINAL_CALENDAR_FILE);
        when(icsScraper.scrapICalendar(anyString())).thenReturn(originalCalendar);

        // when - 업데이트 실행
        assertDoesNotThrow(() -> academicEventUpdater.update());

        // then - DB에 저장된 데이터 검증
        Map<String, AcademicEvent> savedEvents = academicEventQueryPort.findAllInEventUidsAsMap(originEventUids);

        assertThat(savedEvents).hasSize(2);

        // 하계방학 이벤트 검증
        AcademicEvent startEvent = savedEvents.get(updatedEventUids.get(0));
        assertEventFields(startEvent, "하계방학", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 6, 22, 0, 0),
                LocalDateTime.of(2024, 9, 2, 0, 0));

        // 폐강교과목 공지 이벤트 검증
        AcademicEvent endEvent = savedEvents.get(updatedEventUids.get(1));
        assertThat(endEvent).isNotNull();
        assertEventFields(endEvent, "폐강교과목 공지(1차)(9:00~)", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 9, 2, 0, 0),
                LocalDateTime.of(2024, 9, 3, 0, 0));
    }

    @Test
    @DisplayName("업데이트된 캘린더 파일로 기존 데이터를 업데이트하고 신규 데이터를 추가한다")
    void should_update_existing_and_add_new_events() throws IOException, ParserException {
        // given - 먼저 원본 데이터 저장
        Calendar originalCalendar = loadCalendarFromFile(ORIGINAL_CALENDAR_FILE);
        when(icsScraper.scrapICalendar(anyString())).thenReturn(originalCalendar);
        academicEventUpdater.update();

        // 초기 데이터 확인
        Map<String, AcademicEvent> initialEvents = academicEventQueryPort.findAllInEventUidsAsMap(originEventUids);
        assertThat(initialEvents).hasSize(2);

        AcademicEvent originalStartEvent = initialEvents.get(originEventUids.get(0));
        Long originalEventId = originalStartEvent.getId();

        // when - 업데이트된 캘린더 파일로 다시 업데이트
        Calendar updatedCalendar = loadCalendarFromFile(UPDATED_CALENDAR_FILE);
        when(icsScraper.scrapICalendar(anyString())).thenReturn(updatedCalendar);
        assertDoesNotThrow(() -> academicEventUpdater.update());

        // then - 업데이트 후 데이터 검증
        Map<String, AcademicEvent> updatedEvents = academicEventQueryPort.findAllInEventUidsAsMap(updatedEventUids);

        assertThat(updatedEvents).hasSize(3); // 기존 2개 + 신규 1개

        // 하계방학 이벤트 검증
        AcademicEvent startEvent = updatedEvents.get(updatedEventUids.get(0));
        assertEventFields(startEvent, "하계방학", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 6, 22, 0, 0),
                LocalDateTime.of(2024, 9, 2, 0, 0));

        // 폐강교과목 공지 이벤트 검증
        AcademicEvent endEvent = updatedEvents.get(updatedEventUids.get(1));
        assertThat(endEvent).isNotNull();
        assertEventFields(endEvent, "폐강교과목 공지(1차)(9:00~)", 1, Transparent.OPAQUE, true,
                LocalDateTime.of(2024, 9, 3, 0, 0),
                LocalDateTime.of(2024, 9, 4, 0, 0));

        // 신규 공지 이벤트 검증
        AcademicEvent newEvent = updatedEvents.get(updatedEventUids.get(2));
        assertEventFields(newEvent, "김한주가넣은가짜학사일정", 0, Transparent.OPAQUE, true,
                LocalDateTime.of(2028, 2, 29, 0, 0),
                LocalDateTime.of(2028, 2, 29, 0, 0));
    }

    private Calendar loadCalendarFromFile(String filePath) throws IOException, ParserException {
        CalendarBuilder builder = new CalendarBuilder();
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            return builder.build(inputStream);
        }
    }

    public void assertEventFields(AcademicEvent academicEvent, String summary, Integer sequence, Transparent transparent,
                                  Boolean notifyEnabled, LocalDateTime startDate, LocalDateTime endDate) {
        assertThat(academicEvent).isNotNull();
        assertThat(academicEvent.getSummary()).isEqualTo(summary);
        assertThat(academicEvent.getSequence()).isEqualTo(sequence);
        assertThat(academicEvent.getTransparent()).isEqualTo(transparent);
        assertThat(academicEvent.getNotifyEnabled()).isEqualTo(notifyEnabled);
        assertThat(academicEvent.getStartTime()).isEqualTo(startDate);
        assertThat(academicEvent.getEndTime()).isEqualTo(endDate);
    }
}
package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.worker.scrap.calendar.IcsScraper;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@DisplayName("AcademicEventUpdater 통합 테스트")
class AcademicEventUpdaterTest extends IntegrationTestSupport {

    @Autowired
    private AcademicEventUpdater academicEventUpdater;

    @Autowired
    private AcademicEventQueryPort academicEventQueryPort;

    @MockBean
    private IcsScraper icsScraper;

    @MockBean
    private FeatureFlags featureFlags;

    private final String ORIGINAL_CALENDAR_FILE = "src/test/resources/calendar/academic-calendar-origin.ics";
    private final String UPDATED_CALENDAR_FILE = "src/test/resources/calendar/academic-calendar-updated.ics";

    private final List<String> testEventUids = List.of("040000008200E00074C5B7101A82E0080000000033C0B88EDFBAD9010000000000000000100000006ECC99F08500CA4096B671977C2912F7", // 하계방학
            "040000008200E00074C5B7101A82E0080000000014125995CCA1DA01000000000000000010000000FA658BFA03493F4A9597C68E4BF868BF", //폐강교과목 공지(수정됨)
            "140000008200E00074C5B7101A82E00800000000C1545CD275FFDB01000000000000000010000000A6974AF4EE07FD42979F2F3DA1D208DC"); //김한주 생일(새로 추가됨)

    private Calendar originalCalendar;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        try {
            originalCalendar = loadCalendarFromFile(ORIGINAL_CALENDAR_FILE);
        } catch (IOException | ParserException e) {
            throw new RuntimeException(e);
        }
        when(featureFlags.isEnabled(any())).thenReturn(true);
    }

    @Test
    @DisplayName("원본 캘린더 파일로 초기 데이터를 DB에 저장한다")
    void should_save_initial_calendar_data_to_db() throws IOException, ParserException {
        // given - 원본 캘린더 파일을 스크랩하도록 Mock 설정
        mockingScrapCalendar(originalCalendar);

        // when - 업데이트 실행
        assertDoesNotThrow(() -> academicEventUpdater.update());

        // then - DB에 저장된 데이터 검증
        List<AcademicEvent> allEvents = academicEventQueryPort.findAll();
        assertThat(allEvents).hasSize(198);

        AcademicEvent event1 = allEvents.stream()
                .filter(event -> event.getEventUid().equals(testEventUids.get(0)))
                .findFirst()
                .get();

        AcademicEvent event2 = allEvents.stream()
                .filter(event -> event.getEventUid().equals(testEventUids.get(1)))
                .findFirst()
                .get();

        // 하계방학 이벤트 검증
        assertEventFields(event1, "하계방학", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 6, 22, 0, 0),
                LocalDateTime.of(2024, 9, 2, 0, 0));

        // 폐강교과목 공지 이벤트 검증
        assertEventFields(event2, "폐강교과목 공지(1차)(9:00~)", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 9, 2, 0, 0),
                LocalDateTime.of(2024, 9, 3, 0, 0));
    }

    @Test
    @DisplayName("업데이트된 캘린더 파일로 기존 데이터를 업데이트하고 신규 데이터를 추가한다")
    void should_update_existing_and_add_new_events() throws IOException, ParserException {
        // given - 먼저 원본 데이터 저장
        mockingScrapCalendar(originalCalendar);
        academicEventUpdater.update();

        // when - 업데이트된 캘린더 파일로 다시 업데이트
        Calendar updatedCalendar = loadCalendarFromFile(UPDATED_CALENDAR_FILE);
        mockingScrapCalendar(updatedCalendar);
        assertDoesNotThrow(() -> academicEventUpdater.update());

        // then - DB에 저장된 데이터 검증
        List<AcademicEvent> allEvents = academicEventQueryPort.findAll();
        assertThat(allEvents).hasSize(199);

        AcademicEvent event1 = allEvents.stream()
                .filter(event -> event.getEventUid().equals(testEventUids.get(0)))
                .findFirst()
                .get();

        AcademicEvent event2 = allEvents.stream()
                .filter(event -> event.getEventUid().equals(testEventUids.get(1)))
                .findFirst()
                .get();

        AcademicEvent event3 = allEvents.stream()
                .filter(event -> event.getEventUid().equals(testEventUids.get(2)))
                .findFirst()
                .get();

        // 하계방학 이벤트 검증
        assertEventFields(event1, "하계방학", 0, Transparent.TRANSPARENT, false,
                LocalDateTime.of(2024, 6, 22, 0, 0),
                LocalDateTime.of(2024, 9, 2, 0, 0));

        // 폐강교과목 공지 이벤트 검증
        assertEventFields(event2, "폐강교과목 공지(1차)(9:00~)", 1, Transparent.OPAQUE, true,
                LocalDateTime.of(2024, 9, 3, 0, 0),
                LocalDateTime.of(2024, 9, 4, 0, 0));

        // 신규 공지 이벤트 검증
        assertEventFields(event3, "김한주가넣은가짜학사일정", 0, Transparent.OPAQUE, true,
                LocalDateTime.of(2028, 2, 29, 0, 0),
                LocalDateTime.of(2028, 2, 29, 0, 0));
    }

    @Test
    @DisplayName("ICS 스크랩 중 IOException 발생 시 정상적으로 처리된다")
    void should_handle_io_exception_gracefully() throws IOException, ParserException {
        // given - IOException 발생하도록 Mock 설정
        doThrow(new IOException("네트워크 연결 실패")).when(icsScraper).scrapAcademicCalendar();

        // when & then - 예외가 발생해도 정상 종료되어야 함
        assertDoesNotThrow(() -> academicEventUpdater.update());

        // DB에는 변경사항이 없어야 함
        List<AcademicEvent> allEvents = academicEventQueryPort.findAll();
        assertThat(allEvents).isEmpty();
    }

    private Calendar loadCalendarFromFile(String filePath) throws IOException, ParserException {
        CalendarBuilder builder = new CalendarBuilder();
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            return builder.build(inputStream);
        }
    }

    private void mockingScrapCalendar(Calendar originalCalendar) throws IOException, ParserException {
        when(icsScraper.scrapAcademicCalendar()).thenReturn(originalCalendar);
        // IcsParser 모킹은 실제 파서를 사용 (정상 테스트에서는 실제 파싱 결과 필요)
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
package com.kustacks.kuring.worker.parser.calendar;

import com.kustacks.kuring.worker.parser.calendar.dto.IcsCalendarResult;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("파서 : IcsParser")
class IcsParserTest {

    private static IcsParser icsParser;
    private static Calendar sampleCalendar;

    @BeforeAll
    static void setUp() throws IOException, ParserException {
        icsParser = new IcsParser();

        // 테스트 리소스에서 학사일정 데이터 로드 (한 번만 실행)
        InputStream in = IcsParserTest.class.getResourceAsStream("/calendar/academic-calendar-origin.ics");
        CalendarBuilder builder = new CalendarBuilder();
        sampleCalendar = builder.build(in);
    }

    @DisplayName("파싱 결과가 예상되는 개수의 이벤트를 포함한다")
    @Test
    void parse_expected_number_of_events() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);
        List<IcsEvent> events = result.events();

        // then
        long opaqueCount = events.stream()
                .filter(event -> event.transp().equals("OPAQUE"))
                .count();
        long transparentCount = events.stream()
                .filter(event -> event.transp().equals("TRANSPARENT"))
                .count();

        assertAll(
                () -> assertThat(events).hasSize(198),
                () -> assertThat(opaqueCount).isEqualTo(22),
                () -> assertThat(transparentCount).isEqualTo(176)
        );
    }

    @DisplayName("파싱된 캘린더 속성이 올바르게 추출된다")
    @Test
    void parse_calendar_properties() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.properties()).isNotNull(),
                () -> assertThat(result.properties().method()).isEqualTo("PUBLISH"),
                () -> assertThat(result.properties().prodId()).isEqualTo("Microsoft Exchange Server 2010"),
                () -> assertThat(result.properties().version()).isEqualTo("2.0"),
                () -> assertThat(result.properties().xWrCalName()).isEqualTo("학사일정")
        );
    }

    @DisplayName("타임존 정보가 파싱된다")
    @Test
    void parse_timezone_info() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);

        // then
        assertThat(result.timeZone()).isNotNull();
        assertThat(result.timeZone().tzid()).isEqualTo("Korea Standard Time");
    }

    @DisplayName("타임존 STANDARD 정보가 올바르게 파싱된다")
    @Test
    void parse_timezone_standard_info() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);

        // then
        assertAll(
                () -> assertThat(result.timeZone()).isNotNull(),
                () -> assertThat(result.timeZone().standard()).isNotNull(),
                () -> assertThat(result.timeZone().standard().dtStart()).isEqualTo("16010101T000000"),
                () -> assertThat(result.timeZone().standard().tzOffsetFrom()).isEqualTo("+0900"),
                () -> assertThat(result.timeZone().standard().tzOffsetTo()).isEqualTo("+0900")
        );
    }

    @DisplayName("타임존 DAYLIGHT 정보가 올바르게 파싱된다")
    @Test
    void parse_timezone_daylight_info() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);

        // then
        assertAll(
                () -> assertThat(result.timeZone()).isNotNull(),
                () -> assertThat(result.timeZone().daylight()).isNotNull(),
                () -> assertThat(result.timeZone().daylight().dtStart()).isEqualTo("16010101T000000"),
                () -> assertThat(result.timeZone().daylight().tzOffsetFrom()).isEqualTo("+0900"),
                () -> assertThat(result.timeZone().daylight().tzOffsetTo()).isEqualTo("+0900")
        );
    }

    @DisplayName("하계방학 이벤트가 정확하게 파싱된다")
    @Test
    void parse_summer_vacation_event() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);
        List<IcsEvent> events = result.events();

        // then
        IcsEvent summerVacation = events.stream()
                .filter(event -> "하계방학".equals(event.summary()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("하계방학 이벤트를 찾을 수 없습니다"));

        assertAll(
                () -> assertThat(summerVacation.uid()).startsWith("040000008200E00074C5B7101A82E0080000000033C0B88EDFBAD901"),
                () -> assertThat(summerVacation.dtstart()).isEqualTo("20240622"),
                () -> assertThat(summerVacation.dtend()).isEqualTo("20240902"),
                () -> assertThat(summerVacation.classType()).isEqualTo("PUBLIC"),
                () -> assertThat(summerVacation.priority()).isEqualTo("5"),
                () -> assertThat(summerVacation.transp()).isEqualTo("TRANSPARENT"),
                () -> assertThat(summerVacation.status()).isEqualTo("CONFIRMED"),
                () -> assertThat(summerVacation.sequence()).isEqualTo("0"),
                () -> assertThat(summerVacation.location()).isEmpty()
        );
    }

    @DisplayName("날짜만 포함된 이벤트가 올바르게 파싱된다")
    @Test
    void parse_date_only_events() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);
        List<IcsEvent> events = result.events();

        // then
        String dateOnlyRegex = "\\d{8}";
        List<IcsEvent> dateOnlyEvents = events.stream()
                .filter(event -> event.dtstart() != null && event.dtstart().matches(dateOnlyRegex))
                .toList();

        IcsEvent dateOnlyEvent = dateOnlyEvents.get(0);
        assertAll(
                () -> assertThat(dateOnlyEvents).isNotEmpty(),
                () -> assertThat(dateOnlyEvent.dtstart()).matches(dateOnlyRegex),
                () -> assertThat(dateOnlyEvent.dtend()).matches(dateOnlyRegex)
        );
    }

    @DisplayName("시간 포함된 이벤트가 올바르게 파싱된다")
    @Test
    void parse_datetime_events() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);
        List<IcsEvent> events = result.events();

        // then
        String timeIncludedRegex = "\\d{8}T\\d{6}(Z)?";
        List<IcsEvent> timeIncludedEvents = events.stream()
                .filter(event -> event.dtstart() != null && event.dtstart().matches(timeIncludedRegex))
                .toList();

        IcsEvent timeEvent = timeIncludedEvents.get(0);
        assertAll(
                () -> assertThat(timeIncludedEvents).isNotEmpty(),
                () -> assertThat(timeEvent.dtstart()).matches(timeIncludedRegex),
                () -> assertThat(timeEvent.dtend()).matches(timeIncludedRegex)
        );
    }
}
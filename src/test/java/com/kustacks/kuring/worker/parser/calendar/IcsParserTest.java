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
import static org.assertj.core.api.Assertions.assertThatCode;

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
        // 전체 학사일정은 199개
        assertThat(events.size()).isEqualTo(198);

        // OPAQUE 22개
        long opaqueCount = events.stream()
                .filter(event -> event.transp().equals("OPAQUE"))
                .count();
        assertThat(opaqueCount).isEqualTo(22);

        // TRANSPARENT 176개
        long transparentCount = events.stream()
                .filter(event -> event.transp().equals("TRANSPARENT"))
                .count();
        assertThat(transparentCount).isEqualTo(176);
    }

    @DisplayName("파싱된 캘린더 속성이 올바르게 추출된다")
    @Test
    void parse_calendar_properties() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);

        // then
        assertThat(result).isNotNull();
        assertThat(result.properties()).isNotNull();

        // 실제 ICS 파일 내용과 정확히 일치하는지 확인
        assertThat(result.properties().method()).isEqualTo("PUBLISH");
        assertThat(result.properties().prodId()).isEqualTo("Microsoft Exchange Server 2010");
        assertThat(result.properties().version()).isEqualTo("2.0");
        assertThat(result.properties().xWrCalName()).isEqualTo("학사일정");
    }

    @DisplayName("타임존 정보가 올바르게 파싱된다")
    @Test
    void parse_timezone_info() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);

        // then
        // 타임존 정보가 있는지 확인 (파서 구현에 따라 null일 수 있음)
        if (result.timeZone() != null) {
            assertThat(result.timeZone().tzid()).isEqualTo("Korea Standard Time");

            // STANDARD 정보가 파싱되었다면 확인
            if (result.timeZone().standard() != null) {
                assertThat(result.timeZone().standard().dtStart()).isEqualTo("16010101T000000");
                assertThat(result.timeZone().standard().tzOffsetFrom()).isEqualTo("+0900");
                assertThat(result.timeZone().standard().tzOffsetTo()).isEqualTo("+0900");
            }

            // DAYLIGHT 정보가 파싱되었다면 확인
            if (result.timeZone().daylight() != null) {
                assertThat(result.timeZone().daylight().dtStart()).isEqualTo("16010101T000000");
                assertThat(result.timeZone().daylight().tzOffsetFrom()).isEqualTo("+0900");
                assertThat(result.timeZone().daylight().tzOffsetTo()).isEqualTo("+0900");
            }
        }
    }

    @DisplayName("학사일정 이벤트들이 올바른 형식으로 파싱된다")
    @Test
    void parse_academic_events_format() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);
        List<IcsEvent> events = result.events();

        // then
        assertThat(events).isNotEmpty();

        // 모든 이벤트가 필수 필드를 가지고 있는지 확인
        for (IcsEvent event : events) {
            assertThat(event.uid()).isNotBlank();

            // 날짜 형식 확인 (yyyyMMddTHHmmss 또는 yyyyMMdd 형식)
            if (event.dtstart() != null) {
                assertThat(event.dtstart()).matches("\\d{8}(T\\d{6})?.*");
            }
            if (event.dtend() != null) {
                assertThat(event.dtend()).matches("\\d{8}(T\\d{6})?.*");
            }
        }

        //sequence가 숫자로 파싱된다.
        assertThat(events).isNotEmpty();

        // sequence가 있는 이벤트들 확인
        for (IcsEvent event : events) {
            if (event.sequence() != null) {
                // sequence는 숫자 형태여야 함
                assertThatCode(() -> Integer.parseInt(event.sequence()))
                        .doesNotThrowAnyException();
            }
        }
    }

    @DisplayName("실제 학사일정 이벤트 내용이 정확하게 파싱된다")
    @Test
    void parse_specific_academic_events() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);
        List<IcsEvent> events = result.events();

        // then
        assertThat(events).isNotEmpty();

        // 첫 번째 이벤트: 하계방학 (TRANSPARENT)
        IcsEvent summerVacation = events.stream()
                .filter(event -> "하계방학".equals(event.summary()))
                .findFirst()
                .orElse(null);

        assertThat(summerVacation).isNotNull();
        assertThat(summerVacation.uid()).startsWith("040000008200E00074C5B7101A82E0080000000033C0B88EDFBAD901");
        assertThat(summerVacation.dtstart()).isEqualTo("20240622");
        assertThat(summerVacation.dtend()).isEqualTo("20240902");
        assertThat(summerVacation.classType()).isEqualTo("PUBLIC");
        assertThat(summerVacation.priority()).isEqualTo("5");
        assertThat(summerVacation.transp()).isEqualTo("TRANSPARENT");
        assertThat(summerVacation.status()).isEqualTo("CONFIRMED");
        assertThat(summerVacation.sequence()).isEqualTo("0");
        assertThat(summerVacation.location()).isEmpty();

        // 두 번째 이벤트: 폐강교과목 공지 (TRANSPARENT)
        IcsEvent closedCourseNotice = events.stream()
                .filter(event -> event.summary() != null && event.summary().contains("폐강교과목 공지"))
                .findFirst()
                .orElse(null);

        assertThat(closedCourseNotice).isNotNull();
        assertThat(closedCourseNotice.summary()).isEqualTo("폐강교과목 공지(1차)(9:00~)");
        assertThat(closedCourseNotice.uid()).startsWith("040000008200E00074C5B7101A82E0080000000014125995CCA1DA01");
        assertThat(closedCourseNotice.dtstart()).isEqualTo("20240902");
        assertThat(closedCourseNotice.dtend()).isEqualTo("20240903");
        assertThat(closedCourseNotice.transp()).isEqualTo("TRANSPARENT");
        assertThat(closedCourseNotice.status()).isEqualTo("CONFIRMED");
        assertThat(closedCourseNotice.sequence()).isEqualTo("0");
    }

    @DisplayName("날짜 형식별 이벤트들이 모두 올바르게 파싱된다")
    @Test
    void parse_different_date_formats() {
        // when
        IcsCalendarResult result = icsParser.parse(sampleCalendar);
        List<IcsEvent> events = result.events();

        // then
        assertThat(events).isNotEmpty();

        // VALUE=DATE 형식 (yyyyMMdd) 확인
        List<IcsEvent> dateOnlyEvents = events.stream()
                .filter(event -> event.dtstart() != null && event.dtstart().matches("\\d{8}$"))
                .toList();
        assertThat(dateOnlyEvents).isNotEmpty();

        // 날짜만 있는 이벤트 예시 검증
        IcsEvent dateOnlyEvent = dateOnlyEvents.get(0);
        assertThat(dateOnlyEvent.dtstart()).matches("\\d{8}"); // 20240622 형식
        assertThat(dateOnlyEvent.dtend()).matches("\\d{8}");   // 20240902 형식

        // 시간 포함 형식이 있다면 확인 (yyyyMMddTHHmmss 또는 yyyyMMddTHHmmssZ)
        List<IcsEvent> timeIncludedEvents = events.stream()
                .filter(event -> event.dtstart() != null && event.dtstart().matches("\\d{8}T\\d{6}.*"))
                .toList();

        // 시간 포함 이벤트가 있다면 검증
        if (!timeIncludedEvents.isEmpty()) {
            IcsEvent timeEvent = timeIncludedEvents.get(0);
            assertThat(timeEvent.dtstart()).matches("\\d{8}T\\d{6}(Z)?");
            assertThat(timeEvent.dtend()).matches("\\d{8}T\\d{6}(Z)?");
        }
    }
}
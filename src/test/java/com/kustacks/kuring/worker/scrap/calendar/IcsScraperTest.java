package com.kustacks.kuring.worker.scrap.calendar;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("IcsScraper 단위 테스트")
class IcsScraperTest {

    @Autowired
    private IcsScraper icsScraper;

    @Autowired
    private ResourceLoader resourceLoader;

    private String mockIcsPath;

    @BeforeEach
    void setUp() throws IOException {
        // given
        Resource resource = resourceLoader.getResource("classpath:calendar/academic-calendar-origin.ics");
        mockIcsPath = resource.getURI().toString();
    }

    @Test
    @DisplayName("Mock ICS 파일을 정상적으로 스크랩할 수 있다")
    void should_scrap_mock_ics_file_successfully() throws IOException, ParserException {

        // when
        Calendar calendar = icsScraper.scrapICalendar(mockIcsPath);

        // then
        assertAll(
                () -> assertNotNull(calendar),
                () -> assertThat(calendar.getComponents()).isNotEmpty(),
                () -> assertThat(calendar.getProductId()).isNotNull(),
                () -> assertThat(calendar.getVersion()).isNotNull(),
                () -> assertThat(calendar.getMethod()).isNotNull()
        );
    }

    @Test
    @DisplayName("스크랩된 캘린더에서 이벤트를 추출할 수 있다")
    void should_extract_events_from_scraped_calendar() throws IOException, ParserException {
        // when
        Calendar calendar = icsScraper.scrapICalendar(mockIcsPath);
        var events = calendar.getComponents(Component.VEVENT);

        // then
        assertThat(events).isNotEmpty();

        VEvent firstEvent = (VEvent) events.iterator().next();
        assertAll(
                () -> assertThat(firstEvent.getUid()).isNotNull(),
                () -> assertThat(firstEvent.getSummary()).isNotNull(),
                () -> assertThat(firstEvent.getDateTimeStart()).isNotNull(),
                () -> assertThat(firstEvent.getEndDate()).isNotNull()
        );
    }

    @Test
    @DisplayName("잘못된 URL로 스크랩 시 IOException이 발생한다")
    void should_throw_IOException_when_invalid_url() {
        // given
        String invalidUrl = "invalid-url";

        // when & then
        assertThrows(
                java.net.MalformedURLException.class,
                () -> icsScraper.scrapICalendar(invalidUrl)
        );
    }

    @Test
    @DisplayName("존재하지 않는 파일 경로로 스크랩 시 IOException이 발생한다")
    void should_throw_IOException_when_file_not_exists() {
        // given
        String nonExistentFilePath = "file:///non/existent/path/test.ics";

        // when & then
        assertThrows(
                IOException.class,
                () -> icsScraper.scrapICalendar(nonExistentFilePath)
        );
    }
}
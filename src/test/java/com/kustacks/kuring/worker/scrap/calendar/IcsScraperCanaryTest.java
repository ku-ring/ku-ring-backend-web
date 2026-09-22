package com.kustacks.kuring.worker.scrap.calendar;

import net.fortuna.ical4j.model.Calendar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@EnabledIfEnvironmentVariable(named = "RUN_EXTERNAL_TESTS", matches = "true")
@DisplayName("학사일정 ICS URL canary 테스트")
class IcsScraperCanaryTest {

    @Test
    @DisplayName("Outlook에서 유효한 학사일정 ICS를 가져온다")
    void should_fetch_valid_academic_calendar_from_outlook() {
        IcsScraper scraper = new IcsScraper();
        scraper.init();

        Calendar calendar = assertTimeoutPreemptively(
                Duration.ofSeconds(30),
                scraper::scrapAcademicCalendar
        );

        assertThat(calendar).isNotNull();
    }
}

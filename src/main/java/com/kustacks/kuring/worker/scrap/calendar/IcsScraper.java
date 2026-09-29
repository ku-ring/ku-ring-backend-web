package com.kustacks.kuring.worker.scrap.calendar;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Component
public class IcsScraper {

    private static final String ACADEMIC_CALENDAR_OUTLOOK_ICS_URL = "https://outlook.office365.com/owa/calendar/0a5e22263dff43609142c77a5ad9b947@konkuk.ac.kr/30fb04e9e46b4b6c9b3e7ec5bd26bdd59948333863255802754/calendar.ics";

    private URL url;

    @PostConstruct
    public void init() {
        try {
            url = new URL(ACADEMIC_CALENDAR_OUTLOOK_ICS_URL);
        } catch (MalformedURLException e) {
            log.error("잘못된 URL 설정: {}", ACADEMIC_CALENDAR_OUTLOOK_ICS_URL, e);
        }
    }

    public Calendar scrapAcademicCalendar() throws IOException, ParserException {
        try (InputStream in = url.openStream()) {
            CalendarBuilder builder = new CalendarBuilder();
            return builder.build(in);
        }
    }
}

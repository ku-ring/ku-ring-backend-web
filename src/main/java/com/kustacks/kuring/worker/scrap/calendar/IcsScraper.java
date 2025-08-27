package com.kustacks.kuring.worker.scrap.calendar;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class IcsScraper {

    public Calendar scrapICalendar(String icsUrl) throws IOException, ParserException {
        URL url = new URL(icsUrl);

        try (InputStream in = url.openStream()) {
            CalendarBuilder builder = new CalendarBuilder();
            return builder.build(in);
        }
    }
}

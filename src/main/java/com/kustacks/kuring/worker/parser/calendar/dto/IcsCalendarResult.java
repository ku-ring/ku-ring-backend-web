package com.kustacks.kuring.worker.parser.calendar.dto;

import java.util.List;

public record IcsCalendarResult(
        IcsCalendarProperties properties,
        IcsTimeZone timeZone,
        List<IcsEvent> events
) {
    public static IcsCalendarResult from(IcsCalendarProperties properties, IcsTimeZone icsTimeZone, List<IcsEvent> events) {
        return new IcsCalendarResult(properties, icsTimeZone, events);
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }
}

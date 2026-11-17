package com.kustacks.kuring.worker.parser.calendar.dto;

import lombok.Builder;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;

import java.util.Objects;

@Builder
public record IcsEvent(
        String uid,
        String summary,
        String description,
        Property dtstart,
        Property dtend,
        String classType,
        String priority,
        String dtstamp,
        String transp,
        String status,
        String sequence,
        String location
) {
    public boolean isDtstartAllDay() {
        return isAllDayEvent(dtstart);
    }

    public boolean isDtendAllDay() {
        return isAllDayEvent(dtend);
    }

    private boolean isAllDayEvent(Property property) {
        if (Objects.isNull(property)) {
            return false;
        }

        Parameter valueParameter =
                property.getParameter("VALUE").orElse(null);

        return Objects.nonNull(valueParameter)
                && "DATE".equalsIgnoreCase(valueParameter.getValue());
    }
}

package com.kustacks.kuring.worker.parser.calendar;

import com.kustacks.kuring.worker.parser.calendar.dto.IcsCalendarProperties;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsCalendarResult;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsTimeZone;
import com.kustacks.kuring.worker.parser.calendar.dto.TimeZoneDetail;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class IcsParser {

    public IcsCalendarResult parse(Calendar calendar) {
        PropertyList propertyList = calendar.getPropertyList();
        IcsCalendarProperties properties = parseProperties(propertyList);

        ComponentList<CalendarComponent> componentList = calendar.getComponentList();
        IcsTimeZone timeZone = parseTimeZone(componentList);
        List<IcsEvent> events = parseEvents(componentList);

        return IcsCalendarResult.from(properties, timeZone, events);
    }

    private IcsCalendarProperties parseProperties(PropertyList propertyList) {
        String method = getPropertyValue(propertyList, Property.METHOD);
        String prodId = getPropertyValue(propertyList, Property.PRODID);
        String version = getPropertyValue(propertyList, Property.VERSION);
        String xWrCalName = getPropertyValue(propertyList, "X-WR-CALNAME");
        return new IcsCalendarProperties(method, prodId, version, xWrCalName);
    }

    private IcsTimeZone parseTimeZone(ComponentList<CalendarComponent> componentList) {
        return componentList.getAll().stream()
                .filter(component -> Component.VTIMEZONE.equals(component.getName()))
                .findFirst()
                .map(this::buildIcsTimeZone)
                .orElse(null);
    }

    private IcsTimeZone buildIcsTimeZone(Component vTimeZone) {
        String tzid = getPropertyValue(vTimeZone, Property.TZID);

        TimeZoneDetail standard = vTimeZone.getProperty("STANDARD")
                .map(this::buildTimeZoneDetail)
                .orElse(null);

        TimeZoneDetail daylight = vTimeZone.getProperty("DAYLIGHT")
                .map(this::buildTimeZoneDetail)
                .orElse(null);

        return new IcsTimeZone(tzid, standard, daylight);
    }

    private TimeZoneDetail buildTimeZoneDetail(Property tzProperty) {
        if (Objects.isNull(tzProperty)) {
            return null;
        }

        String dtStart = getPropertyValue(tzProperty, Property.DTSTART);
        String tzOffsetFrom = getPropertyValue(tzProperty, Property.TZOFFSETFROM);
        String tzOffsetTo = getPropertyValue(tzProperty, Property.TZOFFSETTO);
        return new TimeZoneDetail(dtStart, tzOffsetFrom, tzOffsetTo);
    }

    private List<IcsEvent> parseEvents(ComponentList<CalendarComponent> componentList) {
        return componentList.getAll().stream()
                .filter(component -> Component.VEVENT.equals(component.getName()))
                .map(this::buildIcsEvent)
                .collect(Collectors.toList());
    }

    private IcsEvent buildIcsEvent(Component vEvent) {
        return new IcsEvent(
                getPropertyValue(vEvent, Property.UID),
                getPropertyValue(vEvent, Property.SUMMARY),
                getPropertyValue(vEvent, Property.DESCRIPTION),
                getPropertyValue(vEvent, Property.DTSTART),
                getPropertyValue(vEvent, Property.DTEND),
                getPropertyValue(vEvent, Property.CLASS),
                getPropertyValue(vEvent, Property.PRIORITY),
                getPropertyValue(vEvent, Property.DTSTAMP),
                getPropertyValue(vEvent, Property.TRANSP),
                getPropertyValue(vEvent, Property.STATUS),
                getPropertyValue(vEvent, Property.SEQUENCE),
                getPropertyValue(vEvent, Property.LOCATION)
        );
    }

    private String getPropertyValue(Component component, String propertyName) {
        Property property = component.getProperty(propertyName)
                .orElse(null);
        if (Objects.nonNull(property)) {
            return property.getValue();
        } else {
            return null;
        }
    }

    private String getPropertyValue(Property property, String propertyName) {
        Parameter parameter = property.getParameter(propertyName)
                .orElse(null);
        if (Objects.nonNull(parameter)) {
            return parameter.getValue();
        } else {
            return null;
        }
    }

    private String getPropertyValue(PropertyList propertyList, String propertyName) {
        Property property = propertyList.getProperty(propertyName)
                .orElse(null);
        if (Objects.nonNull(property)) {
            return property.getValue();
        } else {
            return null;
        }
    }
}

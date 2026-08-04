package com.kustacks.kuring.building.adapter.out.calendar;

import com.kustacks.kuring.building.application.port.out.AcademicPeriodPort;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AcademicEventPeriodAdapter implements AcademicPeriodPort {

    private final AcademicEventQueryPort academicEventQueryPort;

    @Override
    public OperatingPeriod resolve(LocalDate date) {
        return academicEventQueryPort.findEventsBefore(date).stream()
                .map(this::toBoundary)
                .flatMap(Optional::stream)
                .max(Comparator.comparing(PeriodBoundary::startsAt))
                .map(PeriodBoundary::period)
                .orElse(OperatingPeriod.SEMESTER);
    }

    private Optional<PeriodBoundary> toBoundary(AcademicEventReadModel event) {
        String summary = event.summary()
                .replaceAll("\\s", "")
                .toLowerCase(Locale.ROOT);

        if (summary.contains("개강")) {
            return Optional.of(new PeriodBoundary(event.startTime(), OperatingPeriod.SEMESTER));
        }

        if (summary.contains("방학")) {
            return Optional.of(new PeriodBoundary(event.startTime(), OperatingPeriod.VACATION));
        }

        return Optional.empty();
    }

    private record PeriodBoundary(LocalDateTime startsAt, OperatingPeriod period) {
    }
}

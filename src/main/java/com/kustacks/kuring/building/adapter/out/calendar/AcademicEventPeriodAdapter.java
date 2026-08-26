package com.kustacks.kuring.building.adapter.out.calendar;

import com.kustacks.kuring.building.application.port.out.AcademicPeriodQueryPort;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.calendar.application.port.in.AcademicEventQueryUseCase;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventLookupCommand;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AcademicEventPeriodAdapter implements AcademicPeriodQueryPort {

    private static final int RECENT_BOUNDARY_MONTHS = 5;

    private final AcademicEventQueryUseCase academicEventQueryUseCase;

    @Override
    public OperatingPeriod determineOperatingPeriod(LocalDate date) {
        LocalDateTime recentBoundaryThreshold = date.minusMonths(RECENT_BOUNDARY_MONTHS).atStartOfDay();

        return academicEventQueryUseCase.getAcademicEventsByDateRange(
                        new AcademicEventLookupCommand(null, date)
                ).stream()
                .map(this::toBoundary)
                .flatMap(Optional::stream)
                .filter(boundary -> !boundary.startsAt().isBefore(recentBoundaryThreshold))
                .max(Comparator.comparing(PeriodBoundary::startsAt))
                .map(PeriodBoundary::period)
                .orElseGet(() -> resolveFallback(date));
    }

    private Optional<PeriodBoundary> toBoundary(AcademicEventResult event) {
        String summary = event.summary()
                .replaceAll("\\s", "")
                .toLowerCase(Locale.ROOT);

        if (summary.contains("개강") && !summary.contains("계절")) {
            return Optional.of(new PeriodBoundary(event.startTime(), OperatingPeriod.SEMESTER));
        }

        if (summary.contains("방학")) {
            return Optional.of(new PeriodBoundary(event.startTime(), OperatingPeriod.VACATION));
        }

        return Optional.empty();
    }

    private OperatingPeriod resolveFallback(LocalDate date) {
        return switch (date.getMonth()) {
            case JANUARY, FEBRUARY, JULY, AUGUST -> OperatingPeriod.VACATION;
            default -> OperatingPeriod.SEMESTER;
        };
    }

    private record PeriodBoundary(LocalDateTime startsAt, OperatingPeriod period) {
    }
}

package com.kustacks.kuring.calendar.application.service;

import com.kustacks.kuring.calendar.application.port.in.AcademicEventQueryUseCase;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventLookupCommand;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventResult;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.application.service.exception.AcademicEventException;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AcademicEventQueryService implements AcademicEventQueryUseCase {

    private final AcademicEventQueryPort academicEventQueryPort;

    @Override
    public List<AcademicEventResult> getAcademicEventsByDateRange(AcademicEventLookupCommand command) {
        LocalDate startDate = command.startDate();
        LocalDate endDate = command.endDate();

        validateStartAndEndDates(startDate, endDate);

        List<AcademicEventReadModel> events = getEventsByDates(startDate, endDate);
        return convertModelsToResults(events);
    }

    private void validateStartAndEndDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new AcademicEventException(ErrorCode.ACADEMIC_EVENT_INVALID_RANGE);
        }
    }

    private List<AcademicEventReadModel> getEventsByDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return academicEventQueryPort.findAllEventReadModels();
        }

        if (startDate == null) {
            return academicEventQueryPort.findEventsBefore(endDate);
        }

        if (endDate == null) {
            return academicEventQueryPort.findEventsAfter(startDate);
        }

        return academicEventQueryPort.findEventsBetweenDate(startDate, endDate);
    }

    private List<AcademicEventResult> convertModelsToResults(List<AcademicEventReadModel> events) {
        return events.stream()
                .map(this::convertModelToResult)
                .toList();
    }

    private AcademicEventResult convertModelToResult(AcademicEventReadModel model) {
        return new AcademicEventResult(
                model.id(),
                model.eventUid(),
                model.summary(),
                model.description(),
                model.category(),
                model.transparent().name(),
                model.sequence(),
                model.notifyEnabled(),
                model.startTime(),
                model.endTime()
        );
    }
}

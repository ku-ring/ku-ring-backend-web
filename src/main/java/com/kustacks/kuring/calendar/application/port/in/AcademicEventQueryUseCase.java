package com.kustacks.kuring.calendar.application.port.in;

import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventLookupCommand;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventResult;

import java.util.List;

public interface AcademicEventQueryUseCase {

    List<AcademicEventResult> getAcademicEventsByDateRange(AcademicEventLookupCommand command);
}
package com.kustacks.kuring.building.adapter.out.calendar;

import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.calendar.application.port.in.AcademicEventQueryUseCase;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventLookupCommand;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("어댑터 : AcademicEventPeriodAdapter")
@ExtendWith(MockitoExtension.class)
class AcademicEventPeriodAdapterTest {

    @Mock
    private AcademicEventQueryUseCase academicEventQueryUseCase;

    @InjectMocks
    private AcademicEventPeriodAdapter academicEventPeriodAdapter;

    @Test
    @DisplayName("가장 최근 개강 또는 방학 일정을 기준으로 운영기간을 판단한다")
    void determine_period_from_latest_academic_boundary() {
        // given
        LocalDate today = LocalDate.of(2026, 7, 20);
        when(academicEventQueryUseCase.getAcademicEventsByDateRange(
                new AcademicEventLookupCommand(null, today)
        )).thenReturn(List.of(
                event(1L, "2026학년도 1학기 개강", LocalDateTime.of(2026, 3, 2, 0, 0)),
                event(2L, "하계 방학", LocalDateTime.of(2026, 6, 22, 0, 0)),
                event(3L, "하계 계절학기 수강정정", LocalDateTime.of(2026, 7, 1, 0, 0))
        ));

        // when
        OperatingPeriod period = academicEventPeriodAdapter.determineOperatingPeriod(today);

        // then
        assertThat(period).isEqualTo(OperatingPeriod.VACATION);
    }

    @Test
    @DisplayName("판단할 학사일정이 없으면 날짜를 기준으로 운영기간을 판단한다")
    void determine_semester_when_boundary_does_not_exist() {
        // given
        LocalDate today = LocalDate.of(2026, 3, 2);
        when(academicEventQueryUseCase.getAcademicEventsByDateRange(
                new AcademicEventLookupCommand(null, today)
        )).thenReturn(List.of());

        // when
        OperatingPeriod period = academicEventPeriodAdapter.determineOperatingPeriod(today);

        // then
        assertThat(period).isEqualTo(OperatingPeriod.SEMESTER);
    }

    @Test
    @DisplayName("가장 최근 학사일정이 5개월보다 오래되면 날짜를 기준으로 운영기간을 판단한다")
    void determine_period_from_date_when_boundary_is_stale() {
        // given
        LocalDate today = LocalDate.of(2026, 8, 20);
        when(academicEventQueryUseCase.getAcademicEventsByDateRange(
                new AcademicEventLookupCommand(null, today)
        )).thenReturn(List.of(
                event(1L, "2025학년도 2학기 개강", LocalDateTime.of(2025, 9, 1, 0, 0))
        ));

        // when
        OperatingPeriod period = academicEventPeriodAdapter.determineOperatingPeriod(today);

        // then
        assertThat(period).isEqualTo(OperatingPeriod.VACATION);
    }

    private AcademicEventResult event(Long id, String summary, LocalDateTime startsAt) {
        return new AcademicEventResult(
                id,
                "event-" + id,
                summary,
                null,
                "ACADEMIC_OPERATION_EVENT",
                "TRANSPARENT",
                0,
                false,
                startsAt,
                startsAt.plusDays(1)
        );
    }
}

package com.kustacks.kuring.building.adapter.out.calendar;

import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.domain.Transparent;
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
    private AcademicEventQueryPort academicEventQueryPort;

    @InjectMocks
    private AcademicEventPeriodAdapter adapter;

    @Test
    @DisplayName("가장 최근 개강 또는 방학 일정을 기준으로 운영기간을 판단한다")
    void resolve_period_from_latest_academic_boundary() {
        // given
        LocalDate today = LocalDate.of(2026, 7, 20);
        when(academicEventQueryPort.findEventsBefore(today)).thenReturn(List.of(
                event(1L, "2026학년도 1학기 개강", LocalDateTime.of(2026, 3, 2, 0, 0)),
                event(2L, "하계 방학", LocalDateTime.of(2026, 6, 22, 0, 0)),
                event(3L, "하계 계절학기 수강정정", LocalDateTime.of(2026, 7, 1, 0, 0))
        ));

        // when
        OperatingPeriod period = adapter.resolve(today);

        // then
        assertThat(period).isEqualTo(OperatingPeriod.VACATION);
    }

    @Test
    @DisplayName("판단할 학사일정이 없으면 학기중으로 판단한다")
    void resolve_semester_when_boundary_does_not_exist() {
        // given
        LocalDate today = LocalDate.of(2026, 3, 2);
        when(academicEventQueryPort.findEventsBefore(today)).thenReturn(List.of());

        // when
        OperatingPeriod period = adapter.resolve(today);

        // then
        assertThat(period).isEqualTo(OperatingPeriod.SEMESTER);
    }

    private AcademicEventReadModel event(Long id, String summary, LocalDateTime startsAt) {
        return new AcademicEventReadModel(
                id,
                "event-" + id,
                summary,
                null,
                "ACADEMIC_OPERATION_EVENT",
                Transparent.TRANSPARENT,
                0,
                false,
                startsAt,
                startsAt.plusDays(1)
        );
    }
}

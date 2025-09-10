package com.kustacks.kuring.calendar.application.service;

import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventLookupCommand;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventResult;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.application.service.exception.AcademicEventException;
import com.kustacks.kuring.calendar.domain.Transparent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : AcademicEventQueryService")
@ExtendWith(MockitoExtension.class)
class AcademicEventQueryServiceTest {

    @Mock
    private AcademicEventQueryPort academicEventQueryPort;

    @InjectMocks
    private AcademicEventQueryService academicEventQueryService;

    private List<AcademicEventReadModel> mockReadModels =
            List.of(
                    new AcademicEventReadModel(
                            1L,
                            "test-uid-1",
                            "2025년 1학기 개강",
                            "2025년 1학기가 시작됩니다.",
                            "ACADEMIC",
                            Transparent.TRANSPARENT,
                            1,
                            true,
                            LocalDateTime.of(2025, 3, 1, 9, 0),
                            LocalDateTime.of(2025, 3, 1, 18, 0)
                    ),

                    new AcademicEventReadModel(
                            2L,
                            "test-uid-2",
                            "기말고사 기간",
                            "2025년 1학기 기말고사 기간입니다.",
                            "EXAM",
                            Transparent.OPAQUE,
                            1,
                            true,
                            LocalDateTime.of(2025, 6, 15, 9, 0),
                            LocalDateTime.of(2025, 6, 20, 18, 0)
                    )
            );

    @DisplayName("전체 학사일정 조회 성공")
    @Test
    void get_all_academic_events_success() {
        // given
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(null, null);

        when(academicEventQueryPort.findAllEventReadModels()).thenReturn(mockReadModels);

        // when
        List<AcademicEventResult> results = academicEventQueryService.getAcademicEventsByDateRange(command);

        // then
        assertThat(results).hasSize(2);
        verify(academicEventQueryPort).findAllEventReadModels();
    }

    @DisplayName("날짜 범위로 학사일정 조회 성공")
    @Test
    void get_academic_events_by_date_range_success() {
        // given
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(startDate, endDate);

        when(academicEventQueryPort.findEventsBetweenDate(startDate, endDate)).thenReturn(List.of(mockReadModels.get(0)));

        // when
        List<AcademicEventResult> results = academicEventQueryService.getAcademicEventsByDateRange(command);

        // then
        Assertions.assertAll(
                () -> assertThat(results).hasSize(1),
                () -> verify(academicEventQueryPort).findEventsBetweenDate(startDate, endDate)
        );
    }

    @DisplayName("시작일만으로 학사일정 조회 성공")
    @Test
    void get_academic_events_by_start_date_only_success() {
        // given
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(startDate, null);

        when(academicEventQueryPort.findEventsAfter(startDate)).thenReturn(mockReadModels);

        // when
        List<AcademicEventResult> results = academicEventQueryService.getAcademicEventsByDateRange(command);

        // then
        assertThat(results).hasSize(2);

        verify(academicEventQueryPort).findEventsAfter(startDate);
    }

    @DisplayName("종료일만으로 학사일정 조회 성공")
    @Test
    void get_academic_events_by_end_date_only_success() {
        // given
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(null, endDate);

        when(academicEventQueryPort.findEventsBefore(endDate)).thenReturn(mockReadModels);

        // when
        List<AcademicEventResult> results = academicEventQueryService.getAcademicEventsByDateRange(command);

        // then
        assertThat(results).hasSize(2);

        verify(academicEventQueryPort).findEventsBefore(endDate);
    }

    @DisplayName("시작일이 종료일보다 늦은 경우 예외 반환")
    @Test
    void get_academic_events_start_date_after_end_date() {
        // given - 시작일이 종료일보다 늦음
        LocalDate startDate = LocalDate.of(2025, 12, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(startDate, endDate);

        // when, then
        assertThatThrownBy(() -> academicEventQueryService.getAcademicEventsByDateRange(command))
                .isInstanceOf(AcademicEventException.class);
    }

    @DisplayName("이벤트가 존재하지 않는 기간인 경우 빈 리스트 조회")
    @Test
    void get_academic_events_future_date() {
        // given
        LocalDate futureStartDate = LocalDate.of(2099, 1, 1);
        LocalDate futureEndDate = LocalDate.of(2099, 12, 31);
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(futureStartDate, futureEndDate);

        when(academicEventQueryPort.findEventsBetweenDate(futureStartDate, futureEndDate)).thenReturn(List.of());

        // when
        List<AcademicEventResult> results = academicEventQueryService.getAcademicEventsByDateRange(command);

        // then
        assertThat(results).isEmpty();

        verify(academicEventQueryPort).findEventsBetweenDate(futureStartDate, futureEndDate);
    }

    @DisplayName("경계값 테스트 - 정확한 날짜 일치")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "'정확히 시작일에 시작', 2025-03-01, 2025-03-01, 2, '2025년 1학기 개강'",
            "'정확히 종료일에 종료', 2025-06-20, 2025-06-20, 1, '기말고사 기간'"
    })
    void get_academic_events_boundary_test_exact_date_match(String scenario, LocalDate queryStartDate, LocalDate queryEndDate, int expectedSize, String expectedSummary) {
        // given
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(queryStartDate, queryEndDate);
        List<AcademicEventReadModel> mockData = expectedSize == 1 ? List.of(mockReadModels.get(1)) : mockReadModels;
        when(academicEventQueryPort.findEventsBetweenDate(queryStartDate, queryEndDate)).thenReturn(mockData);

        // when
        List<AcademicEventResult> results = academicEventQueryService.getAcademicEventsByDateRange(command);

        // then
        assertThat(results).hasSize(expectedSize);
        assertThat(results.get(0).summary()).isEqualTo(expectedSummary);
        verify(academicEventQueryPort).findEventsBetweenDate(queryStartDate, queryEndDate);
    }

    @DisplayName("경계값 테스트 - 다양한 기간 겹침 시나리오")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "'요청 시작일 = 이벤트 종료일', 2025-06-20, 2025-06-30",
            "'요청 종료일 = 이벤트 시작일', 2025-06-10, 2025-06-15",
            "'이벤트가 요청 기간을 완전 포함', 2025-06-17, 2025-06-19"
    })
    void get_academic_events_boundary_test_various_overlap_scenarios(String scenario, LocalDate requestStartDate, LocalDate requestEndDate) {
        // given
        AcademicEventLookupCommand command = new AcademicEventLookupCommand(requestStartDate, requestEndDate);
        when(academicEventQueryPort.findEventsBetweenDate(requestStartDate, requestEndDate)).thenReturn(List.of(mockReadModels.get(1)));

        // when
        List<AcademicEventResult> results = academicEventQueryService.getAcademicEventsByDateRange(command);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).summary()).isEqualTo("기말고사 기간");
        verify(academicEventQueryPort).findEventsBetweenDate(requestStartDate, requestEndDate);
    }
}
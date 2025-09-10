package com.kustacks.kuring.calendar.adapter.in.web;

import com.kustacks.kuring.calendar.adapter.in.web.dto.AcademicEventLookupResponse;
import com.kustacks.kuring.calendar.application.port.in.AcademicEventQueryUseCase;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventLookupCommand;
import com.kustacks.kuring.calendar.application.port.in.dto.AcademicEventResult;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ACADEMIC_EVENT_SEARCH_SUCCESS;

@Tag(name = "Academic Event-Query", description = "학사일정 조회")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/academic-events")
public class AcademicEventQueryApiV2 {

    private final AcademicEventQueryUseCase academicEventQueryUseCase;

    @Operation(summary = "학사일정 조회", description = "시작일과 종료일 사이의 학사일정을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<AcademicEventLookupResponse>>> getAcademicEvents(
            @Parameter(description = "조회 시작일 (yyyy-MM-dd 형식)")
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @Parameter(description = "조회 종료일 (yyyy-MM-dd 형식)")
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate
    ) {
        List<AcademicEventResult> result = academicEventQueryUseCase.getAcademicEventsByDateRange(new AcademicEventLookupCommand(startDate, endDate));

        var response = result
                .stream()
                .map(AcademicEventLookupResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(ACADEMIC_EVENT_SEARCH_SUCCESS, response));
    }
}

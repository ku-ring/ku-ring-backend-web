package com.kustacks.kuring.calendar.adapter.out.exception;

import com.kustacks.kuring.calendar.application.service.exception.AcademicEventException;
import com.kustacks.kuring.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AcademicEventExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> academicEventExceptionHandler(AcademicEventException exception) {
        log.warn("[AcademicEventException] {}", exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }
}

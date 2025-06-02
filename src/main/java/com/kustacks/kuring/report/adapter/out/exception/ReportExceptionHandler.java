package com.kustacks.kuring.report.adapter.out.exception;

import com.kustacks.kuring.common.dto.ErrorResponse;
import com.kustacks.kuring.report.application.service.exception.ReportBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ReportExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> reportExceptionHandler(ReportBusinessException exception) {
        log.warn("[ReportBusinessException] {}", exception.getMessage());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }
}

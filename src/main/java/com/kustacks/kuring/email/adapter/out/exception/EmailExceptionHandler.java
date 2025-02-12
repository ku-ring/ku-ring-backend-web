package com.kustacks.kuring.email.adapter.out.exception;

import com.kustacks.kuring.common.dto.ErrorResponse;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EmailExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> emailExceptionHandler(EmailBusinessException exception) {
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(exception.getErrorCode()));
    }
}
